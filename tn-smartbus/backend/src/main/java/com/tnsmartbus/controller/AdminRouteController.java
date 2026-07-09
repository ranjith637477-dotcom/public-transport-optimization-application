package com.tnsmartbus.controller;

import com.tnsmartbus.dto.RouteRequest;
import com.tnsmartbus.entity.Route;
import com.tnsmartbus.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final RouteRepository routeRepository;

    @GetMapping
    public List<Route> list() {
        return routeRepository.findAll();
    }

    @PostMapping
    public Route create(@RequestBody RouteRequest request) {
        Route route = new Route();
        applyRequest(route, request);
        return routeRepository.save(route);
    }

    @PutMapping("/{id}")
    public Route update(@PathVariable UUID id, @RequestBody RouteRequest request) {
        Route route = routeRepository.findById(id).orElseThrow();
        applyRequest(route, request);
        return routeRepository.save(route);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        routeRepository.deleteById(id);
    }

    private void applyRequest(Route route, RouteRequest request) {
        route.setRouteNumber(request.getRouteNumber());
        route.setRouteName(request.getRouteName());
        route.setSourceName(request.getSourceName());
        route.setDestinationName(request.getDestinationName());
        route.setTotalDistanceKm(request.getTotalDistanceKm());
        route.setEstimatedDurationMin(request.getEstimatedDurationMin());
        route.setIsRural(request.getIsRural() != null ? request.getIsRural() : true);
    }
}
