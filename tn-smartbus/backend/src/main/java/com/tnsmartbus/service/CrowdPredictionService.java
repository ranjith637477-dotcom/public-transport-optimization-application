package com.tnsmartbus.service;

import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.repository.CrowdDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Wraps the crowd-level prediction with a three-tier fallback, from most to
 * least reliable:
 *   1. Real conductor-reported occupancy (ConductorService.updateCrowd) if
 *      logged in the last 15 minutes for this trip - ground truth beats any
 *      prediction.
 *   2. The FastAPI AI microservice (see /ai-service), which uses historical
 *      occupancy + time-of-day + school/office timings.
 *   3. A local rule-based heuristic, so the tracking module stays functional
 *      even if the AI service and conductor haven't reported anything yet.
 */
@Service
@RequiredArgsConstructor
public class CrowdPredictionService {

    @Value("${app.ai-service.base-url:http://localhost:8000}")
    private String aiServiceBaseUrl;

    private final CrowdDataRepository crowdDataRepository;
    private final RestClient restClient = RestClient.create();

    public String predictCrowdLevel(Trip trip) {
        var recentReport = crowdDataRepository.findTopByTripIdOrderByRecordedAtDesc(trip.getId());
        if (recentReport.isPresent()) {
            var report = recentReport.get();
            boolean isRecent = report.getRecordedAt().isAfter(java.time.LocalDateTime.now().minusMinutes(15));
            if (isRecent && report.getCrowdLevel() != null) {
                return report.getCrowdLevel();
            }
        }

        try {
            return callAiService(trip);
        } catch (Exception ex) {
            return fallbackRuleBasedCrowd();
        }
    }

    private String callAiService(Trip trip) {
        // Example contract: POST /predict/crowd { tripId, hour, dayOfWeek }
        // Left as a real HTTP call so swapping in the trained model is a one-line change.
        record CrowdResponse(String crowdLevel) {}
        CrowdResponse response = restClient.post()
                .uri(aiServiceBaseUrl + "/predict/crowd")
                .body(new CrowdRequest(trip.getId().toString(),
                        java.time.LocalTime.now().getHour(),
                        java.time.LocalDate.now().getDayOfWeek().toString()))
                .retrieve()
                .body(CrowdResponse.class);
        return response != null ? response.crowdLevel() : fallbackRuleBasedCrowd();
    }

    private String fallbackRuleBasedCrowd() {
        int hour = java.time.LocalTime.now().getHour();
        boolean peak = (hour >= 7 && hour <= 10) || (hour >= 17 && hour <= 20);
        if (peak) return "HIGH";
        boolean shoulder = (hour >= 11 && hour <= 16);
        return shoulder ? "MEDIUM" : "LOW";
    }

    private record CrowdRequest(String tripId, int hour, String dayOfWeek) {}
}
