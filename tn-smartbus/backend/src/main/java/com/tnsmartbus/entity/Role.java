package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name; // PASSENGER, DRIVER, CONDUCTOR, ADMIN, DEPOT_MANAGER, GOV_ADMIN
}
