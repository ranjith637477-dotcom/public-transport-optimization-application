package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaints")
@Data
public class Complaint {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "complaint_type", nullable = false)
    private String complaintType; // DRIVER, CONDUCTOR, BUS, ROAD

    @Column(nullable = false)
    private String description;

    @Column
    private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED, REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
