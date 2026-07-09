package com.tnsmartbus.repository;

import com.tnsmartbus.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BusStopRepository extends JpaRepository<BusStop, UUID> {

    @Query(value = "SELECT * FROM bus_stops b WHERE " +
            "ST_DWithin(b.location, ST_MakePoint(:lng, :lat)::geography, :radiusMeters) " +
            "ORDER BY ST_Distance(b.location, ST_MakePoint(:lng, :lat)::geography)",
            nativeQuery = true)
    List<BusStop> findNearbyStops(double lat, double lng, double radiusMeters);
}
