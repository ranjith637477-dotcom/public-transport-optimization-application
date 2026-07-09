package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Feedback;
import com.tnsmartbus.repository.FeedbackRepository;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public record FeedbackRequest(UUID tripId, BigDecimal busRating, BigDecimal driverRating, String comments) {}

    @PostMapping
    public Feedback submit(@AuthenticationPrincipal String userId, @RequestBody FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        if (request.tripId() != null) {
            feedback.setTrip(tripRepository.findById(request.tripId()).orElse(null));
        }
        feedback.setBusRating(request.busRating());
        feedback.setDriverRating(request.driverRating());
        feedback.setComments(request.comments());
        return feedbackRepository.save(feedback);
    }
}
