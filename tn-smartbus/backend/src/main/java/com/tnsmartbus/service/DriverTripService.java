package com.tnsmartbus.service;

import com.tnsmartbus.dto.DriverStatusRequest;
import com.tnsmartbus.dto.EmergencyAlertRequest;
import com.tnsmartbus.entity.SosAlert;
import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.repository.SosAlertRepository;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Trip lifecycle and status control for the driver role.
 * Every method here is scoped to the authenticated driver's own trip
 * (findByIdAndDriver_User_Id) so one driver cannot start/end or alter
 * another driver's trip even if they guess the trip id.
 */
@Service
@RequiredArgsConstructor
public class DriverTripService {

    private final TripRepository tripRepository;
    private final LiveTrackingService liveTrackingService;
    private final SosAlertRepository sosAlertRepository;
    private final UserRepository userRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Trip startTrip(UUID driverUserId, UUID tripId, double startLat, double startLng) {
        Trip trip = requireOwnedTrip(driverUserId, tripId);
        trip.setStatus("RUNNING");
        trip.setActualStartTime(LocalDateTime.now());
        trip.setCurrentLocation(geometryFactory.createPoint(new Coordinate(startLng, startLat)));
        Trip saved = tripRepository.save(trip);
        liveTrackingService.broadcastFleetSnapshot();
        return saved;
    }

    public Trip endTrip(UUID driverUserId, UUID tripId) {
        Trip trip = requireOwnedTrip(driverUserId, tripId);
        trip.setStatus("COMPLETED");
        trip.setActualEndTime(LocalDateTime.now());
        Trip saved = tripRepository.save(trip);
        liveTrackingService.broadcastFleetSnapshot();
        return saved;
    }

    public void pushLocation(UUID driverUserId, UUID tripId, double lat, double lng, double speed, double heading) {
        requireOwnedTrip(driverUserId, tripId); // ownership check before delegating
        liveTrackingService.updateTripLocation(tripId, lat, lng, speed, heading);
        liveTrackingService.broadcastFleetSnapshot();
    }

    public Trip updateStatus(UUID driverUserId, UUID tripId, DriverStatusRequest request) {
        Trip trip = requireOwnedTrip(driverUserId, tripId);
        if (request.getBreakStatus() != null) trip.setBreakStatus(request.getBreakStatus());
        if (request.getFuelStatus() != null) trip.setFuelStatus(request.getFuelStatus());
        if (request.getBusHealthStatus() != null) trip.setBusHealthStatus(request.getBusHealthStatus());
        return tripRepository.save(trip);
    }

    public SosAlert raiseEmergency(UUID driverUserId, UUID tripId, EmergencyAlertRequest request) {
        Trip trip = requireOwnedTrip(driverUserId, tripId);
        trip.setEmergencyAlertActive(true);
        tripRepository.save(trip);

        SosAlert alert = new SosAlert();
        alert.setUser(userRepository.findById(driverUserId).orElseThrow());
        alert.setTrip(trip);
        alert.setLocation(geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude())));
        return sosAlertRepository.save(alert);
    }

    private Trip requireOwnedTrip(UUID driverUserId, UUID tripId) {
        return tripRepository.findByIdAndDriver_User_Id(tripId, driverUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trip not found or not assigned to this driver: " + tripId));
    }
}
