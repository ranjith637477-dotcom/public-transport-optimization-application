package com.tnsmartbus.service;

import com.tnsmartbus.dto.*;
import com.tnsmartbus.entity.Role;
import com.tnsmartbus.entity.User;
import com.tnsmartbus.repository.RoleRepository;
import com.tnsmartbus.repository.UserRepository;
import com.tnsmartbus.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public void requestOtp(String phoneNumber) {
        otpService.sendOtp(phoneNumber);
    }

    /** Verifies OTP, creates the passenger account if new, and returns a JWT. */
    public AuthResponse verifyOtpAndLogin(OtpVerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtpCode());
        if (!valid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> createPassenger(request.getPhoneNumber()));

        String token = jwtUtil.generateToken(user.getId().toString(), user.getRole().getName());
        return new AuthResponse(token, user.getId().toString(), user.getFullName(), user.getRole().getName());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        Role passengerRole = roleRepository.findByName("PASSENGER")
                .orElseThrow(() -> new IllegalStateException("PASSENGER role not seeded"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(passengerRole);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId().toString(), user.getRole().getName());
        return new AuthResponse(token, user.getId().toString(), user.getFullName(), user.getRole().getName());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getEmailOrPhone())
                .or(() -> userRepository.findByEmail(request.getEmailOrPhone()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId().toString(), user.getRole().getName());
        return new AuthResponse(token, user.getId().toString(), user.getFullName(), user.getRole().getName());
    }

    private User createPassenger(String phoneNumber) {
        Role passengerRole = roleRepository.findByName("PASSENGER")
                .orElseThrow(() -> new IllegalStateException("PASSENGER role not seeded"));
        User user = new User();
        user.setFullName("Passenger");
        user.setPhoneNumber(phoneNumber);
        user.setRole(passengerRole);
        return userRepository.save(user);
    }
}
