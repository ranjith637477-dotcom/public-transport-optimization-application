package com.tnsmartbus.controller;

import com.tnsmartbus.dto.DriverStatusRequest;
import com.tnsmartbus.dto.EmergencyAlertRequest;
import com.tnsmartbus.dto.GpsPushRequest;
import com.tnsmartbus.entity.SosAlert;
import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.service.DriverTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Driver-facing trip control. All endpoints require a JWT with role DRIVER
 * (see SecurityConfig) and are scoped to the calling driver's own trips.
 */
@RestController
@RequestMapping("/api/v1/driver/trips")
@RequiredArgsConstructor
public class DriverController {

    private final DriverTripService driverTripService;

    @PostMapping("/{tripId}/start")
    public Trip startTrip(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                           @RequestParam double lat, @RequestParam double lng) {
        return driverTripService.startTrip(UUID.fromString(userId), tripId, lat, lng);
    }

    @PostMapping("/{tripId}/end")
    public Trip endTrip(@AuthenticationPrincipal String userId, @PathVariable UUID tripId) {
        return driverTripService.endTrip(UUID.fromString(userId), tripId);
    }

    /** Real GPS ingestion path for the driver mobile app - same downstream flow as the simulator. */
    @PostMapping("/{tripId}/location")
    public void pushLocation(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                              @RequestBody GpsPushRequest request) {
        driverTripService.pushLocation(UUID.fromString(userId), tripId,
                request.getLatitude(), request.getLongitude(), request.getSpeedKmph(), request.getHeadingDegrees());
    }

    @PostMapping("/{tripId}/status")
    public Trip updateStatus(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                              @RequestBody DriverStatusRequest request) {
        return driverTripService.updateStatus(UUID.fromString(userId), tripId, request);
    }

    @PostMapping("/{tripId}/emergency")
    public SosAlert raiseEmergency(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                                    @RequestBody EmergencyAlertRequest request) {
        return driverTripService.raiseEmergency(UUID.fromString(userId), tripId, request);
    }
}
