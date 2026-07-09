package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class FareCalculationRequest {
    private String routeNumber;
    private double distanceKm; // pass explicitly, or resolve from route+stops in production
    private String busType;    // ORDINARY, EXPRESS, DELUXE, ULTRA_DELUXE
    private String concessionType; // NONE, STUDENT, SENIOR_CITIZEN
}
