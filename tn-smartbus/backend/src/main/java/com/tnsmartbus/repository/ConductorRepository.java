package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConductorRepository extends JpaRepository<Conductor, UUID> {
    Optional<Conductor> findByUserId(UUID userId);
}
