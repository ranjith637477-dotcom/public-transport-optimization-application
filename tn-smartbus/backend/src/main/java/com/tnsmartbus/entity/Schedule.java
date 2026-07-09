package com.tnsmartbus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "frequency_minutes")
    private Integer frequencyMinutes;

    @Column(name = "days_of_week")
    private String daysOfWeek = "MON,TUE,WED,THU,FRI,SAT,SUN";

    @Column(name = "schedule_type")
    private String scheduleType = "REGULAR"; // REGULAR, HOLIDAY, FESTIVAL

    @Column(name = "is_active")
    private Boolean isActive = true;
}
