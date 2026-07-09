package com.tnsmartbus.repository;

import com.tnsmartbus.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    List<Complaint> findByUserId(UUID userId);
    List<Complaint> findByStatus(String status);
}
