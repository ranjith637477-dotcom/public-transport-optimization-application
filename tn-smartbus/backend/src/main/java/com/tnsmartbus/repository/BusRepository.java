package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusRepository extends JpaRepository<Bus, UUID> {
    List<Bus> findByRegistrationNumberContainingIgnoreCase(String registrationNumber);
}
