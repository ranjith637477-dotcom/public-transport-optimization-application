package com.tnsmartbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyPlanOption {
    private String routeNumber;
    private String routeName;
    private double distanceKm;
    private int estimatedDurationMin;
    private double estimatedFare;
    private boolean directRoute;
}
