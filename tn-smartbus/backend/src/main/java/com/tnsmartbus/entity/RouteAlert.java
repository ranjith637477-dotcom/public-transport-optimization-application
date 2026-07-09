package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "route_alerts")
@Data
public class RouteAlert {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "alert_type", nullable = false)
    private String alertType; // ROAD_BLOCK, DIVERSION, CANCELLED, ACCIDENT, TRAFFIC

    @Column
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
