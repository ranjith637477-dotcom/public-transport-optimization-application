package com.tnsmartbus.service;

import com.tnsmartbus.dto.JourneyPlanOption;
import com.tnsmartbus.dto.JourneyPlanRequest;
import com.tnsmartbus.repository.RouteStopProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds routes that connect a source stop to a destination stop, ranked by
 * minimum travel time. This is the "direct route" case; multi-leg transfers
 * (least walking / minimum fare across two buses) are a natural Phase 6
 * extension once real-time occupancy data exists to weight transfer options.
 */
@Service
@RequiredArgsConstructor
public class JourneyPlannerService {

    private final RouteStopProjectionRepository routeStopProjectionRepository;
    private final FareCalculatorService fareCalculatorService;

    public List<JourneyPlanOption> planJourney(JourneyPlanRequest request) {
        var matches = routeStopProjectionRepository.findDirectRoutes(
                request.getSourceStopName(), request.getDestinationStopName());

        return matches.stream()
                .map(m -> {
                    double distanceKm = m.getDistanceKm() != null ? m.getDistanceKm() : 0;
                    var fareReq = new com.tnsmartbus.dto.FareCalculationRequest();
                    fareReq.setDistanceKm(distanceKm);
                    fareReq.setBusType("ORDINARY");
                    fareReq.setConcessionType("NONE");
                    double fare = fareCalculatorService.calculate(fareReq).getFinalFare();

                    return new JourneyPlanOption(
                            m.getRouteNumber(), m.getRouteName(), distanceKm,
                            m.getDurationMin() != null ? m.getDurationMin() : 0, fare, true);
                })
                .sorted((a, b) -> Integer.compare(a.getEstimatedDurationMin(), b.getEstimatedDurationMin()))
                .collect(Collectors.toList());
    }
}
