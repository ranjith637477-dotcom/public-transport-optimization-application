package com.tnsmartbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lightweight payload pushed to clients every tick.
 * Kept flat (no nested entities) so it serializes fast over WebSocket.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveBusDto {
    private UUID tripId;
    private String busRegistrationNumber;
    private String busType;
    private String routeNumber;
    private String routeName;
    private double latitude;
    private double longitude;
    private double speedKmph;
    private double headingDegrees;
    private String status;
    private int delayMinutes;
    private String crowdLevel;      // LOW, MEDIUM, HIGH
    private int occupiedSeats;
    private int totalSeats;
    private Double etaMinutesToNextStop;
    private String nextStopName;
}
