package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "conductors")
@Data
public class Conductor {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_code", unique = true, nullable = false)
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;
}
