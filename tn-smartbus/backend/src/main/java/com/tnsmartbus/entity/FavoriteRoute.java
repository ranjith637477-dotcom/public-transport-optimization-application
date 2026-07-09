package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite_routes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "route_id"}))
@Data
public class FavoriteRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
}
