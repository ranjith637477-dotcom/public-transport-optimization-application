package com.tnsmartbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BusStopDto {
    private UUID id;
    private String stopName;
    private double latitude;
    private double longitude;
    private String district;
    private boolean isRural;
}
