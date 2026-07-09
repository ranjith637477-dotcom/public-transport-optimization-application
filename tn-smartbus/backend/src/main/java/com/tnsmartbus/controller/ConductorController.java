package com.tnsmartbus.controller;

import com.tnsmartbus.dto.CrowdUpdateRequest;
import com.tnsmartbus.dto.TicketStatsRequest;
import com.tnsmartbus.entity.CrowdData;
import com.tnsmartbus.entity.TicketStatistics;
import com.tnsmartbus.entity.Trip;
import com.tnsmartbus.service.ConductorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Conductor-facing endpoints. Requires JWT with role CONDUCTOR, scoped to
 * trips assigned to the calling conductor.
 */
@RestController
@RequestMapping("/api/v1/conductor/trips")
@RequiredArgsConstructor
public class ConductorController {

    private final ConductorService conductorService;

    @PostMapping("/{tripId}/crowd")
    public CrowdData updateCrowd(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                                  @RequestBody CrowdUpdateRequest request) {
        return conductorService.updateCrowd(UUID.fromString(userId), tripId, request);
    }

    @PostMapping("/{tripId}/tickets")
    public TicketStatistics updateTickets(@AuthenticationPrincipal String userId, @PathVariable UUID tripId,
                                           @RequestBody TicketStatsRequest request) {
        return conductorService.updateTicketStats(UUID.fromString(userId), tripId, request);
    }

    @PostMapping("/{tripId}/complete")
    public Trip completeTrip(@AuthenticationPrincipal String userId, @PathVariable UUID tripId) {
        return conductorService.completeTrip(UUID.fromString(userId), tripId);
    }
}
