package com.tnsmartbus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.LineString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "routes")
@Data
public class Route {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "route_number", unique = true, nullable = false)
    private String routeNumber;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "destination_name", nullable = false)
    private String destinationName;

    @Column(name = "total_distance_km")
    private BigDecimal totalDistanceKm;

    @Column(name = "estimated_duration_min")
    private Integer estimatedDurationMin;

    @Column(name = "is_rural")
    private Boolean isRural = true;

    @JsonIgnore
    @Column(name = "geom", columnDefinition = "geography(LineString,4326)")
    private LineString geom;
}
