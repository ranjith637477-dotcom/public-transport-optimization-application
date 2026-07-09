package com.tnsmartbus.service;

import com.tnsmartbus.dto.LiveBusDto;
import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.repository.CrowdDataRepository;
import com.tnsmartbus.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Central service for the live tracking module.
 *
 * Responsibilities:
 *  - Persist incoming GPS pings (from real devices, driver app, or the simulator)
 *  - Maintain "current location" on the Trip row so REST reads are O(1)
 *  - Broadcast the updated fleet snapshot to all subscribed WebSocket clients
 *
 * This is intentionally the single choke point both the GPS simulator and any
 * real device/driver-app ingestion endpoint call into, so behaviour stays
 * consistent regardless of the location source.
 */
@Service
@RequiredArgsConstructor
public class LiveTrackingService {

    private final TripRepository tripRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CrowdPredictionService crowdPredictionService;
    private final EtaPredictionService etaPredictionService;
    private final CrowdDataRepository crowdDataRepository;

    /** Called by the GPS simulator or a real ingestion endpoint. */
    @Transactional
    public void updateTripLocation(UUID tripId, double lat, double lng, double speedKmph, double heading) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        var geometryFactory = new org.locationtech.jts.geom.GeometryFactory(
                new org.locationtech.jts.geom.PrecisionModel(), 4326);
        var point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(lng, lat));

        trip.setCurrentLocation(point);
        trip.setCurrentSpeedKmph(java.math.BigDecimal.valueOf(speedKmph));
        trip.setHeadingDegrees(java.math.BigDecimal.valueOf(heading));
        if (!"RUNNING".equals(trip.getStatus())) {
            trip.setStatus("RUNNING");
        }
        tripRepository.save(trip);
    }

    /** Builds the current fleet snapshot and pushes it to /topic/buses/live. */
    @Transactional(readOnly = true)
    public void broadcastFleetSnapshot() {
        List<LiveBusDto> snapshot = buildFleetSnapshot();
        messagingTemplate.convertAndSend("/topic/buses/live", snapshot);
    }

    // readOnly = true keeps the Hibernate session open for the duration of this
    // method, so lazy associations (Trip.bus, Trip.route - both FetchType.LAZY)
    // can still be initialized while mapping to DTOs below. Without this, the
    // session closes as soon as findAllRunningTrips() returns, and any lazy
    // getter called afterwards throws LazyInitializationException - this bit
    // the GPS simulator's scheduled tick in practice.
    @Transactional(readOnly = true)
    public List<LiveBusDto> buildFleetSnapshot() {
        List<Trip> runningTrips = tripRepository.findAllRunningTrips();
        return runningTrips.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LiveBusDto> buildNearbySnapshot(double lat, double lng, double radiusMeters) {
        List<Trip> nearbyTrips = tripRepository.findRunningTripsNearby(lat, lng, radiusMeters);
        return nearbyTrips.stream().map(this::toDto).collect(Collectors.toList());
    }

    private LiveBusDto toDto(Trip trip) {
        double lat = trip.getCurrentLocation() != null ? trip.getCurrentLocation().getY() : 0;
        double lng = trip.getCurrentLocation() != null ? trip.getCurrentLocation().getX() : 0;

        String crowdLevel = crowdPredictionService.predictCrowdLevel(trip);
        Double etaMinutes = etaPredictionService.predictEtaToNextStop(trip);
        int occupiedSeats = crowdDataRepository.findTopByTripIdOrderByRecordedAtDesc(trip.getId())
                .map(cd -> cd.getOccupiedSeats() != null ? cd.getOccupiedSeats() : 0)
                .orElse(0);

        return new LiveBusDto(
                trip.getId(),
                trip.getBus().getRegistrationNumber(),
                trip.getBus().getBusType(),
                trip.getRoute().getRouteNumber(),
                trip.getRoute().getRouteName(),
                lat,
                lng,
                trip.getCurrentSpeedKmph() != null ? trip.getCurrentSpeedKmph().doubleValue() : 0,
                trip.getHeadingDegrees() != null ? trip.getHeadingDegrees().doubleValue() : 0,
                trip.getStatus(),
                trip.getDelayMinutes() != null ? trip.getDelayMinutes() : 0,
                crowdLevel,
                occupiedSeats,
                trip.getBus().getTotalSeats(),
                etaMinutes,
                "Next stop" // placeholder until route-stop sequence lookup is wired in
        );
    }
}
