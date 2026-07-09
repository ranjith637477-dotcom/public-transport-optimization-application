package com.tnsmartbus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity
@Table(name = "bus_stops")
@Data
public class BusStop {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "stop_name", nullable = false)
    private String stopName;

    @JsonIgnore
    @Column(name = "location", columnDefinition = "geography(Point,4326)", nullable = false)
    private Point location;

    @Column(name = "district")
    private String district;

    @Column(name = "is_rural")
    private Boolean isRural = true;
}
