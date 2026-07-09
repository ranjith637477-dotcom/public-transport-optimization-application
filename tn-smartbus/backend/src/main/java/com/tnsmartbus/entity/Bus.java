package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "buses")
@Data
public class Bus {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "registration_number", unique = true, nullable = false)
    private String registrationNumber;

    @Column(name = "bus_type", nullable = false)
    private String busType; // ORDINARY, EXPRESS, DELUXE, ULTRA_DELUXE

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "ladies_seats")
    private Integer ladiesSeats = 0;

    @Column(name = "senior_citizen_seats")
    private Integer seniorCitizenSeats = 0;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;
}
