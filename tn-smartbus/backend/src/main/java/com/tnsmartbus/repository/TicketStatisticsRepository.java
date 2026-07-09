package com.tnsmartbus.repository;

import com.tnsmartbus.entity.TicketStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketStatisticsRepository extends JpaRepository<TicketStatistics, UUID> {
    Optional<TicketStatistics> findByTripId(UUID tripId);
}
