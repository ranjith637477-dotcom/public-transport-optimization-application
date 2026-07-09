package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lost_items")
@Data
public class LostItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "item_type")
    private String itemType = "LOST"; // LOST, FOUND

    @Column(name = "contact_info")
    private String contactInfo;

    @Column
    private String status = "OPEN"; // OPEN, CLAIMED, CLOSED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
