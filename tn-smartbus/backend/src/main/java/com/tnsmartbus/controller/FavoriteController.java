package com.tnsmartbus.controller;

import com.tnsmartbus.entity.FavoriteRoute;
import com.tnsmartbus.entity.FavoriteStop;
import com.tnsmartbus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Favorite routes and stops for the authenticated passenger.
 * The user id comes from the JWT principal (set by JwtAuthFilter), so these
 * endpoints require Authorization: Bearer <token>.
 */
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRouteRepository favoriteRouteRepository;
    private final FavoriteStopRepository favoriteStopRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final BusStopRepository busStopRepository;

    @GetMapping("/routes")
    public List<FavoriteRoute> myFavoriteRoutes(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId) {
        return favoriteRouteRepository.findByUserId(UUID.fromString(userId));
    }

    @PostMapping("/routes/{routeId}")
    public FavoriteRoute addFavoriteRoute(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId,
                                           @PathVariable UUID routeId) {
        var fav = new FavoriteRoute();
        fav.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        fav.setRoute(routeRepository.findById(routeId).orElseThrow());
        return favoriteRouteRepository.save(fav);
    }

    @DeleteMapping("/routes/{routeId}")
    public void removeFavoriteRoute(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId,
                                     @PathVariable UUID routeId) {
        favoriteRouteRepository.deleteByUserIdAndRouteId(UUID.fromString(userId), routeId);
    }

    @GetMapping("/stops")
    public List<FavoriteStop> myFavoriteStops(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId) {
        return favoriteStopRepository.findByUserId(UUID.fromString(userId));
    }

    @PostMapping("/stops/{stopId}")
    public FavoriteStop addFavoriteStop(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId,
                                         @PathVariable UUID stopId) {
        var fav = new FavoriteStop();
        fav.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        fav.setStop(busStopRepository.findById(stopId).orElseThrow());
        return favoriteStopRepository.save(fav);
    }

    @DeleteMapping("/stops/{stopId}")
    public void removeFavoriteStop(@org.springframework.security.core.annotation.AuthenticationPrincipal String userId,
                                    @PathVariable UUID stopId) {
        favoriteStopRepository.deleteByUserIdAndStopId(UUID.fromString(userId), stopId);
    }
}
