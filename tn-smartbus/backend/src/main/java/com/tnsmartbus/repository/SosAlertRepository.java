package com.tnsmartbus.repository;

import com.tnsmartbus.entity.SosAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SosAlertRepository extends JpaRepository<SosAlert, java.util.UUID> {
    List<SosAlert> findByStatus(String status);
}
