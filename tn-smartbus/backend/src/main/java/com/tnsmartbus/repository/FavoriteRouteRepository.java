package com.tnsmartbus.repository;

import com.tnsmartbus.entity.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByUserId(UUID userId);
    void deleteByUserIdAndRouteId(UUID userId, UUID routeId);
}
