package com.tnsmartbus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Backs the "complete route view" passenger feature: ordered stop list with timing. */
@Repository
public interface RouteStopDetailRepository extends org.springframework.data.repository.Repository<com.tnsmartbus.entity.Route, UUID> {

    interface RouteStopDetail {
        String getStopName();
        Integer getSequenceOrder();
        Double getDistanceFromSourceKm();
        Integer getAvgTravelTimeMin();
    }

    @Query(value = """
            SELECT s.stop_name AS stopName, rs.sequence_order AS sequenceOrder,
                   rs.distance_from_source_km AS distanceFromSourceKm,
                   rs.avg_travel_time_min AS avgTravelTimeMin
            FROM route_stops rs
            JOIN bus_stops s ON s.id = rs.stop_id
            WHERE rs.route_id = :routeId
            ORDER BY rs.sequence_order
            """, nativeQuery = true)
    List<RouteStopDetail> findStopsForRoute(UUID routeId);
}
