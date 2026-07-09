package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(name = "experience_years")
    private Integer experienceYears = 0;

    @Column
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "is_on_duty")
    private Boolean isOnDuty = false;
}
