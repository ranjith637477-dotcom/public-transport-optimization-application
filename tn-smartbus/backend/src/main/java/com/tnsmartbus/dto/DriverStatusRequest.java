package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class DriverStatusRequest {
    private String breakStatus;      // NONE, ON_BREAK
    private String fuelStatus;       // FULL, HALF, LOW, CRITICAL
    private String busHealthStatus;  // OK, MINOR_ISSUE, NEEDS_MAINTENANCE
}
