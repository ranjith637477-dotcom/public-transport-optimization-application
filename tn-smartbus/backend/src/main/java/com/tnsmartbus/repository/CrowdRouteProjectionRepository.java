package com.tnsmartbus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Backs the "most crowded routes" analytics widget. */
@Repository
public interface CrowdRouteProjectionRepository extends org.springframework.data.repository.Repository<com.tnsmartbus.entity.Route, UUID> {

    interface CrowdedRouteResult {
        String getRouteNumber();
        String getRouteName();
        Double getAvgOccupancyRatio();
    }

    @Query(value = """
            SELECT r.route_number AS routeNumber, r.route_name AS routeName,
                   AVG(cd.occupied_seats::float / NULLIF(b.total_seats, 0)) AS avgOccupancyRatio
            FROM crowd_data cd
            JOIN trips t ON t.id = cd.trip_id
            JOIN routes r ON r.id = t.route_id
            JOIN buses b ON b.id = t.bus_id
            WHERE cd.recorded_at > now() - interval '7 days'
            GROUP BY r.route_number, r.route_name
            ORDER BY avgOccupancyRatio DESC
            LIMIT 5
            """, nativeQuery = true)
    List<CrowdedRouteResult> mostCrowdedRoutes();
}
