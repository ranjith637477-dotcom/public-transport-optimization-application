package com.tnsmartbus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Read-only projection repository backing the journey planner. Kept separate
 * from RouteRepository since it returns a flat projection (not the Route
 * entity) built from a two-way self-join on route_stops.
 */
@Repository
public interface RouteStopProjectionRepository extends org.springframework.data.repository.Repository<com.tnsmartbus.entity.Route, UUID> {

    interface DirectRouteMatch {
        String getRouteNumber();
        String getRouteName();
        Double getDistanceKm();
        Integer getDurationMin();
    }

    @Query(value = """
            SELECT r.route_number AS routeNumber,
                   r.route_name AS routeName,
                   (rs2.distance_from_source_km - rs1.distance_from_source_km) AS distanceKm,
                   (rs2.avg_travel_time_min - rs1.avg_travel_time_min) AS durationMin
            FROM routes r
            JOIN route_stops rs1 ON rs1.route_id = r.id
            JOIN bus_stops s1 ON s1.id = rs1.stop_id
            JOIN route_stops rs2 ON rs2.route_id = r.id
            JOIN bus_stops s2 ON s2.id = rs2.stop_id
            WHERE s1.stop_name = :sourceStopName
              AND s2.stop_name = :destinationStopName
              AND rs1.sequence_order < rs2.sequence_order
            """, nativeQuery = true)
    List<DirectRouteMatch> findDirectRoutes(String sourceStopName, String destinationStopName);
}
