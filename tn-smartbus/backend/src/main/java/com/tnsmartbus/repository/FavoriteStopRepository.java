package com.tnsmartbus.repository;

import com.tnsmartbus.entity.FavoriteStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteStopRepository extends JpaRepository<FavoriteStop, Long> {
    List<FavoriteStop> findByUserId(UUID userId);
    void deleteByUserIdAndStopId(UUID userId, UUID stopId);
}
