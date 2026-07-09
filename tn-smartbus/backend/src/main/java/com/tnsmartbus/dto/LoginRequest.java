package com.tnsmartbus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String emailOrPhone;
    @NotBlank
    private String password;
}
