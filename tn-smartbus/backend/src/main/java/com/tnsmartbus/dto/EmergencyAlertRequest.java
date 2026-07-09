package com.tnsmartbus.dto;

import lombok.Data;

@Data
public class EmergencyAlertRequest {
    private double latitude;
    private double longitude;
    private String note;
}
