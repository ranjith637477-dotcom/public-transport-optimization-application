package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
    Optional<Route> findByRouteNumber(String routeNumber);
}
