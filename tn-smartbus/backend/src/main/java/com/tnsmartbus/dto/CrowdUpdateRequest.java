package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class CrowdUpdateRequest {
    private int occupiedSeats;
    private int standingCount;
}
