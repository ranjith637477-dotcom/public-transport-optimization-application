package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class GpsPushRequest {
    private double latitude;
    private double longitude;
    private double speedKmph;
    private double headingDegrees;
}
