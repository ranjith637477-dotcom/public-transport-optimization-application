package com.tnsmartbus.controller;

import com.tnsmartbus.entity.SosAlert;
import com.tnsmartbus.repository.SosAlertRepository;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Passenger emergency SOS (feature 17 - women's safety / emergency). */
@RestController
@RequestMapping("/api/v1/sos")
@RequiredArgsConstructor
public class PassengerSosController {

    private final SosAlertRepository sosAlertRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public record SosRequest(double latitude, double longitude, UUID tripId) {}

    @PostMapping
    public SosAlert raise(@AuthenticationPrincipal String userId, @RequestBody SosRequest request) {
        SosAlert alert = new SosAlert();
        alert.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        if (request.tripId() != null) {
            alert.setTrip(tripRepository.findById(request.tripId()).orElse(null));
        }
        alert.setLocation(geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude())));
        return sosAlertRepository.save(alert);
    }
}
