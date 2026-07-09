package com.tnsmartbus.service;

import com.tnsmartbus.entity.OtpVerification;
import com.tnsmartbus.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * OTP generation + verification.
 * SMS delivery is stubbed to a log line (log.info) - swap sendSms() for a
 * real gateway (e.g. MSG91, Twilio) in production. Verification logic,
 * expiry (5 min), and one-time-use are fully real.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpVerificationRepository otpRepository;

    public void sendOtp(String phoneNumber) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));

        OtpVerification record = new OtpVerification();
        record.setPhoneNumber(phoneNumber);
        record.setOtpCode(otp);
        record.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(record);

        sendSms(phoneNumber, otp);
    }

    public boolean verifyOtp(String phoneNumber, String otpCode) {
        return otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .filter(o -> !o.getVerified())
                .filter(o -> o.getOtpCode().equals(otpCode))
                .filter(o -> o.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(o -> {
                    o.setVerified(true);
                    otpRepository.save(o);
                    return true;
                })
                .orElse(false);
    }

    private void sendSms(String phoneNumber, String otp) {
        // TODO: integrate real SMS gateway. Logged for demo/dev purposes only.
        log.info("OTP for {} is {} (expires in 5 min)", phoneNumber, otp);
    }
}
