package com.tnsmartbus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Data
public class Trip {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id")
    private Conductor conductor;

    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @Column(name = "status")
    private String status = "SCHEDULED"; // SCHEDULED, RUNNING, COMPLETED, CANCELLED, DELAYED

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @JsonIgnore
    @Column(name = "current_location", columnDefinition = "geography(Point,4326)")
    private Point currentLocation;

    @Column(name = "current_speed_kmph")
    private BigDecimal currentSpeedKmph;

    @Column(name = "heading_degrees")
    private BigDecimal headingDegrees;

    @Column(name = "delay_minutes")
    private Integer delayMinutes = 0;

    @Column(name = "break_status")
    private String breakStatus = "NONE"; // NONE, ON_BREAK

    @Column(name = "fuel_status")
    private String fuelStatus; // FULL, HALF, LOW, CRITICAL

    @Column(name = "bus_health_status")
    private String busHealthStatus = "OK"; // OK, MINOR_ISSUE, NEEDS_MAINTENANCE

    @Column(name = "emergency_alert_active")
    private Boolean emergencyAlertActive = false;
}
