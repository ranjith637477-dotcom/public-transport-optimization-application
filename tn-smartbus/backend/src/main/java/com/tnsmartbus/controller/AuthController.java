package com.tnsmartbus.controller;

import com.tnsmartbus.dto.*;
import com.tnsmartbus.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/request")
    public void requestOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestOtp(request.getPhoneNumber());
    }

    @PostMapping("/otp/verify")
    public AuthResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return authService.verifyOtpAndLogin(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Google login: exchange a Google ID token for a TN SmartBus JWT.
     * TODO: verify the idToken server-side via Google's tokeninfo endpoint
     * or the google-api-client library, extract email/sub, then reuse the
     * same find-or-create logic as OTP login. Left as a stub so the auth
     * module's shape is complete; wiring the actual Google verification
     * call is a follow-up task once OAuth client credentials are issued.
     */
    @PostMapping("/google")
    public String googleLoginPlaceholder(@RequestBody String idToken) {
        throw new UnsupportedOperationException(
                "Google login verification not yet wired - see TODO in AuthController.googleLoginPlaceholder");
    }
}
