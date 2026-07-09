package com.tnsmartbus.repository;

import com.tnsmartbus.entity.CrowdData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrowdDataRepository extends JpaRepository<CrowdData, Long> {
    List<CrowdData> findByTripIdOrderByRecordedAtDesc(UUID tripId);
    Optional<CrowdData> findTopByTripIdOrderByRecordedAtDesc(UUID tripId);
}
