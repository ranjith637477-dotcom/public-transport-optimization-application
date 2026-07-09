package com.tnsmartbus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String otpCode;
}
