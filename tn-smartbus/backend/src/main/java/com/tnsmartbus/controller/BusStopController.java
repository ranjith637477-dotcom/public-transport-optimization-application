package com.tnsmartbus.controller;

import com.tnsmartbus.dto.BusStopDto;
import com.tnsmartbus.repository.BusStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
public class BusStopController {

    private final BusStopRepository busStopRepository;

    @GetMapping("/nearby")
    public List<BusStopDto> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") double radiusMeters) {
        return busStopRepository.findNearbyStops(lat, lng, radiusMeters).stream()
                .map(stop -> new BusStopDto(
                        stop.getId(),
                        stop.getStopName(),
                        stop.getLocation().getY(),
                        stop.getLocation().getX(),
                        stop.getDistrict(),
                        Boolean.TRUE.equals(stop.getIsRural())
                ))
                .collect(Collectors.toList());
    }
}
