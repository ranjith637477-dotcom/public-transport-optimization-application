package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByRouteId(UUID routeId);
}
