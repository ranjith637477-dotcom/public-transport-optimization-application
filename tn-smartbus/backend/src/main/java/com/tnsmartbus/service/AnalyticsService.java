package com.tnsmartbus.service;

import com.tnsmartbus.dto.DashboardStatsDto;
import com.tnsmartbus.repository.BusRepository;
import com.tnsmartbus.repository.ComplaintRepository;
import com.tnsmartbus.repository.CrowdRouteProjectionRepository;
import com.tnsmartbus.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final ComplaintRepository complaintRepository;
    private final CrowdRouteProjectionRepository crowdRouteProjectionRepository;

    public DashboardStatsDto getDashboardStats() {
        long totalBuses = busRepository.count();
        var runningTrips = tripRepository.findAllRunningTrips();
        long runningBuses = runningTrips.size();
        long delayedBuses = runningTrips.stream()
                .filter(t -> t.getDelayMinutes() != null && t.getDelayMinutes() > 5)
                .count();
        double avgDelay = runningTrips.stream()
                .filter(t -> t.getDelayMinutes() != null)
                .mapToInt(t -> t.getDelayMinutes())
                .average()
                .orElse(0.0);

        long completedToday = tripRepository.findByStatus("COMPLETED").stream()
                .filter(t -> t.getTripDate() != null && t.getTripDate().isEqual(LocalDate.now()))
                .count();

        long openComplaints = complaintRepository.findByStatus("OPEN").size();

        return new DashboardStatsDto(totalBuses, runningBuses, delayedBuses, completedToday, openComplaints, avgDelay);
    }

    public List<CrowdRouteProjectionRepository.CrowdedRouteResult> mostCrowdedRoutes() {
        return crowdRouteProjectionRepository.mostCrowdedRoutes();
    }
}
