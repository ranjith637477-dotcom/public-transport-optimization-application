package com.tnsmartbus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RouteRequest {
    private String routeNumber;
    private String routeName;
    private String sourceName;
    private String destinationName;
    private BigDecimal totalDistanceKm;
    private Integer estimatedDurationMin;
    private Boolean isRural;
}
