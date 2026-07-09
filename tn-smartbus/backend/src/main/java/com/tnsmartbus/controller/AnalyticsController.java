package com.tnsmartbus.controller;

import com.tnsmartbus.dto.DashboardStatsDto;
import com.tnsmartbus.repository.CrowdRouteProjectionRepository;
import com.tnsmartbus.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public DashboardStatsDto dashboard() {
        return analyticsService.getDashboardStats();
    }

    @GetMapping("/crowded-routes")
    public List<CrowdRouteProjectionRepository.CrowdedRouteResult> crowdedRoutes() {
        return analyticsService.mostCrowdedRoutes();
    }
}
