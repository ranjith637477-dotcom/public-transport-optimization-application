package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByStatus(String status);

    java.util.Optional<Trip> findByIdAndDriver_User_Id(UUID tripId, UUID userId);

    java.util.Optional<Trip> findByIdAndConductor_User_Id(UUID tripId, UUID userId);

    @Query("SELECT t FROM Trip t WHERE t.status = 'RUNNING'")
    List<Trip> findAllRunningTrips();

    @Query(value = "SELECT * FROM trips t WHERE t.status = 'RUNNING' " +
            "AND ST_DWithin(t.current_location, ST_MakePoint(:lng, :lat)::geography, :radiusMeters)",
            nativeQuery = true)
    List<Trip> findRunningTripsNearby(double lat, double lng, double radiusMeters);
}
