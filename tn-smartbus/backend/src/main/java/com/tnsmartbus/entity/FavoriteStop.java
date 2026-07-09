package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite_stops", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stop_id"}))
@Data
public class FavoriteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stop_id", nullable = false)
    private BusStop stop;
}
