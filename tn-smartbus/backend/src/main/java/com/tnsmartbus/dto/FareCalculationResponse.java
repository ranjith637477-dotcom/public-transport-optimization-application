package com.tnsmartbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FareCalculationResponse {
    private double baseFare;
    private double concessionDiscount;
    private double finalFare;
    private String busType;
}
