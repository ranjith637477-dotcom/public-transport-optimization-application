package com.tnsmartbus.repository;

import com.tnsmartbus.entity.RouteAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RouteAlertRepository extends JpaRepository<RouteAlert, UUID> {
    List<RouteAlert> findByRouteIdAndIsActiveTrue(UUID routeId);
    List<RouteAlert> findByIsActiveTrue();
}
