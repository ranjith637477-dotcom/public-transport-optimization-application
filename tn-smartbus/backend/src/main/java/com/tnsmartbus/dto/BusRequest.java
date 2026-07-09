package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class BusRequest {
    private String registrationNumber;
    private String busType;
    private Integer totalSeats;
    private Integer ladiesSeats;
    private Integer seniorCitizenSeats;
    private String status;
}
