package com.tnsmartbus.service;

import com.tnsmartbus.entity.Trip;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * ETA to next stop. Calls the trained model in the FastAPI AI microservice
 * (traffic/weather/rural-slowdown features); falls back to a simple
 * distance/speed estimate so the tracking module always returns a value
 * even if the AI service is down or not yet started.
 */
@Service
public class EtaPredictionService {

    @Value("${app.ai-service.base-url:http://localhost:8000}")
    private String aiServiceBaseUrl;

    private final RestClient restClient = RestClient.create();

    public Double predictEtaToNextStop(Trip trip) {
        if (trip.getCurrentSpeedKmph() == null || trip.getCurrentSpeedKmph().doubleValue() <= 0) {
            return null;
        }
        // Placeholder distance-to-next-stop until route_stops sequence lookup is wired in.
        double assumedDistanceKm = 2.0;
        boolean isRural = trip.getRoute() != null && Boolean.TRUE.equals(trip.getRoute().getIsRural());

        try {
            record EtaRequest(double distanceKm, int hour, boolean isRural) {}
            record EtaResponse(double etaMinutes) {}

            EtaResponse response = restClient.post()
                    .uri(aiServiceBaseUrl + "/predict/eta")
                    .body(new EtaRequest(assumedDistanceKm, java.time.LocalTime.now().getHour(), isRural))
                    .retrieve()
                    .body(EtaResponse.class);
            if (response != null) return response.etaMinutes();
        } catch (Exception ignored) {
            // AI service unavailable - fall through to the local estimate below.
        }

        double speed = trip.getCurrentSpeedKmph().doubleValue();
        return (assumedDistanceKm / speed) * 60.0;
    }
}
