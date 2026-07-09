package com.tnsmartbus.service;

import com.tnsmartbus.dto.CrowdUpdateRequest;
import com.tnsmartbus.dto.TicketStatsRequest;
import com.tnsmartbus.entity.CrowdData;
import com.tnsmartbus.entity.TicketStatistics;
import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.repository.CrowdDataRepository;
import com.tnsmartbus.repository.TicketStatisticsRepository;
import com.tnsmartbus.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Conductor-reported data. This is what should eventually replace the
 * rule-based crowd fallback in CrowdPredictionService: real occupied-seat
 * and standing-passenger counts logged here become the training signal for
 * the AI microservice (Phase 5), and can also be read directly for "live"
 * crowd level instead of the time-of-day heuristic.
 */
@Service
@RequiredArgsConstructor
public class ConductorService {

    private final TripRepository tripRepository;
    private final CrowdDataRepository crowdDataRepository;
    private final TicketStatisticsRepository ticketStatisticsRepository;

    public CrowdData updateCrowd(UUID conductorUserId, UUID tripId, CrowdUpdateRequest request) {
        Trip trip = requireOwnedTrip(conductorUserId, tripId);

        CrowdData crowdData = new CrowdData();
        crowdData.setTrip(trip);
        crowdData.setOccupiedSeats(request.getOccupiedSeats());
        crowdData.setStandingCount(request.getStandingCount());
        crowdData.setCrowdLevel(classifyCrowd(trip, request));
        return crowdDataRepository.save(crowdData);
    }

    public TicketStatistics updateTicketStats(UUID conductorUserId, UUID tripId, TicketStatsRequest request) {
        Trip trip = requireOwnedTrip(conductorUserId, tripId);

        TicketStatistics stats = ticketStatisticsRepository.findByTripId(tripId)
                .orElseGet(() -> {
                    TicketStatistics s = new TicketStatistics();
                    s.setTrip(trip);
                    return s;
                });
        stats.setTicketsIssued(request.getTicketsIssued());
        stats.setTotalRevenue(request.getTotalRevenue());
        stats.setUpdatedAt(LocalDateTime.now());
        return ticketStatisticsRepository.save(stats);
    }

    public Trip completeTrip(UUID conductorUserId, UUID tripId) {
        Trip trip = requireOwnedTrip(conductorUserId, tripId);
        trip.setStatus("COMPLETED");
        trip.setActualEndTime(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    /** Occupancy-ratio based classification - a real number, not a placeholder. */
    private String classifyCrowd(Trip trip, CrowdUpdateRequest request) {
        int totalSeats = trip.getBus().getTotalSeats();
        double ratio = totalSeats > 0 ? (double) request.getOccupiedSeats() / totalSeats : 0;
        if (request.getStandingCount() > 10 || ratio >= 0.9) return "HIGH";
        if (request.getStandingCount() > 0 || ratio >= 0.6) return "MEDIUM";
        return "LOW";
    }

    private Trip requireOwnedTrip(UUID conductorUserId, UUID tripId) {
        return tripRepository.findByIdAndConductor_User_Id(tripId, conductorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trip not found or not assigned to this conductor: " + tripId));
    }
}
