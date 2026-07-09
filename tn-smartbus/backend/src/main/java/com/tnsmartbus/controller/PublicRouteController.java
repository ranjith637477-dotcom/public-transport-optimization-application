package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Route;
import com.tnsmartbus.repository.RouteAlertRepository;
import com.tnsmartbus.repository.RouteRepository;
import com.tnsmartbus.repository.RouteStopDetailRepository;
import com.tnsmartbus.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Public, read-only route info for the passenger app: full route list,
 * complete stop-by-stop view (feature 6), schedules (feature 7), and
 * active alerts (feature 11).
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class PublicRouteController {

    private final RouteRepository routeRepository;
    private final RouteStopDetailRepository routeStopDetailRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteAlertRepository routeAlertRepository;

    @GetMapping
    public List<Route> listAll() {
        return routeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable UUID id) {
        Route route = routeRepository.findById(id).orElseThrow();
        var stops = routeStopDetailRepository.findStopsForRoute(id);
        var schedules = scheduleRepository.findByRouteId(id);
        var alerts = routeAlertRepository.findByRouteIdAndIsActiveTrue(id);
        return Map.of("route", route, "stops", stops, "schedules", schedules, "alerts", alerts);
    }

    @GetMapping("/alerts")
    public List<com.tnsmartbus.entity.RouteAlert> allActiveAlerts() {
        return routeAlertRepository.findByIsActiveTrue();
    }
}
