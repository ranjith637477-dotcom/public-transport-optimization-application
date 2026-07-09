package com.tnsmartbus.scheduler;

import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.service.LiveTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Dummy GPS generator required by the spec: rural TNSTC buses often have no
 * live hardware, so this simulator advances every RUNNING trip a small step
 * along its route every tick, standing in for a real GPS device or the
 * driver app's location push. Swap this class out (or disable via
 * app.gps-simulator.enabled=false) once real GPS ingestion is connected -
 * both paths call the same LiveTrackingService.updateTripLocation(), so the
 * rest of the system is unaffected.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GpsSimulatorScheduler {

    private final TripRepository tripRepository;
    private final LiveTrackingService liveTrackingService;

    @Value("${app.gps-simulator.enabled:true}")
    private boolean enabled;

    // Tracks simulated heading per trip so movement looks continuous instead of jittery
    private final Map<UUID, Double> simulatedHeadings = new HashMap<>();

    @Scheduled(fixedDelayString = "${app.gps-simulator.tick-interval-ms:5000}")
    public void tick() {
        if (!enabled) return;

        List<Trip> runningTrips = tripRepository.findAllRunningTrips();
        if (runningTrips.isEmpty()) return;

        for (Trip trip : runningTrips) {
            advanceTrip(trip);
        }
        liveTrackingService.broadcastFleetSnapshot();
    }

    private void advanceTrip(Trip trip) {
        if (trip.getCurrentLocation() == null) {
            log.warn("Trip {} has no starting location, skipping simulation tick", trip.getId());
            return;
        }

        double lat = trip.getCurrentLocation().getY();
        double lng = trip.getCurrentLocation().getX();

        double heading = simulatedHeadings.computeIfAbsent(trip.getId(),
                id -> ThreadLocalRandom.current().nextDouble(0, 360));

        // Small random heading drift so the route looks organic rather than a straight line
        heading += ThreadLocalRandom.current().nextDouble(-8, 8);
        heading = (heading + 360) % 360;
        simulatedHeadings.put(trip.getId(), heading);

        double speedKmph = ThreadLocalRandom.current().nextDouble(20, 55);

        // Convert speed + heading into a lat/lng delta for this tick's interval
        double tickSeconds = 5.0;
        double distanceKm = speedKmph * (tickSeconds / 3600.0);
        double distanceDegrees = distanceKm / 111.0; // approx km per degree latitude

        double radians = Math.toRadians(heading);
        double newLat = lat + distanceDegrees * Math.cos(radians);
        double newLng = lng + distanceDegrees * Math.sin(radians) / Math.cos(Math.toRadians(lat));

        liveTrackingService.updateTripLocation(trip.getId(), newLat, newLng, speedKmph, heading);
    }
}
