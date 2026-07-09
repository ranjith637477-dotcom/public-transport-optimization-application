package com.tnsmartbus.controller;

import com.tnsmartbus.dto.LiveBusDto;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.service.LiveTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final LiveTrackingService liveTrackingService;
    private final TripRepository tripRepository;

    /** REST fallback for clients that poll instead of using the WebSocket feed. */
    @GetMapping("/live")
    public List<LiveBusDto> getAllLiveBuses() {
        return liveTrackingService.buildFleetSnapshot();
    }

    /** Buses within radiusMeters of a point - used for the "nearby buses" passenger feature. */
    @GetMapping("/nearby")
    public List<LiveBusDto> getNearbyBuses(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "2000") double radiusMeters) {
        return liveTrackingService.buildNearbySnapshot(lat, lng, radiusMeters);
    }

    /** Manual GPS ingestion endpoint for a real device or the driver app. */
    @PostMapping("/{tripId}/location")
    public void pushLocation(
            @PathVariable UUID tripId,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "0") double speedKmph,
            @RequestParam(defaultValue = "0") double heading) {
        liveTrackingService.updateTripLocation(tripId, lat, lng, speedKmph, heading);
        liveTrackingService.broadcastFleetSnapshot();
    }
}
