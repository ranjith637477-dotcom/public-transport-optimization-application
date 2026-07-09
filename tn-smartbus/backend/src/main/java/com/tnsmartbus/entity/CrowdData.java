package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "crowd_data")
@Data
public class CrowdData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "occupied_seats")
    private Integer occupiedSeats = 0;

    @Column(name = "standing_count")
    private Integer standingCount = 0;

    @Column(name = "crowd_level")
    private String crowdLevel; // LOW, MEDIUM, HIGH

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();
}
