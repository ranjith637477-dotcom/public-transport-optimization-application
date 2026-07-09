package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class JourneyPlanRequest {
    private String sourceStopName;
    private String destinationStopName;
}
