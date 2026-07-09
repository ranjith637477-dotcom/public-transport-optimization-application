package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Bus;
import com.tnsmartbus.repository.BusRepository;
import com.tnsmartbus.repository.RouteSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Unified search across bus number, route number, source/destination, and intermediate stops. */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final BusRepository busRepository;
    private final RouteSearchRepository routeSearchRepository;

    @GetMapping
    public Map<String, Object> search(@RequestParam String query) {
        List<Bus> buses = busRepository.findByRegistrationNumberContainingIgnoreCase(query);
        List<RouteSearchRepository.RouteSearchResult> routes = routeSearchRepository.search(query);
        return Map.of("buses", buses, "routes", routes);
    }
}
