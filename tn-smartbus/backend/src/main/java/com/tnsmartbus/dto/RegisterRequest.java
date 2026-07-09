package com.tnsmartbus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String phoneNumber;
    @Email
    private String email;
    private String password; // optional if registering purely via OTP
}
