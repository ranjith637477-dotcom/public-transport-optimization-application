package com.tnsmartbus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteSearchRepository extends org.springframework.data.repository.Repository<com.tnsmartbus.entity.Route, UUID> {

    interface RouteSearchResult {
        java.util.UUID getRouteId();
        String getRouteNumber();
        String getRouteName();
        String getSourceName();
        String getDestinationName();
    }

    @Query(value = """
            SELECT DISTINCT r.id AS routeId, r.route_number AS routeNumber, r.route_name AS routeName,
                   r.source_name AS sourceName, r.destination_name AS destinationName
            FROM routes r
            LEFT JOIN route_stops rs ON rs.route_id = r.id
            LEFT JOIN bus_stops s ON s.id = rs.stop_id
            WHERE LOWER(r.route_number) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.route_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.source_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(r.destination_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(s.stop_name) LIKE LOWER(CONCAT('%', :query, '%'))
            """, nativeQuery = true)
    List<RouteSearchResult> search(String query);
}
