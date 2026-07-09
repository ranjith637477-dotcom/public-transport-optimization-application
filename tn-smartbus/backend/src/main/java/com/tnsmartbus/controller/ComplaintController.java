package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Complaint;
import com.tnsmartbus.repository.ComplaintRepository;
import com.tnsmartbus.repository.TripRepository;
import com.tnsmartbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public record ComplaintRequest(String complaintType, String description, UUID tripId) {}

    @PostMapping
    public Complaint file(@AuthenticationPrincipal String userId, @RequestBody ComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        complaint.setComplaintType(request.complaintType());
        complaint.setDescription(request.description());
        if (request.tripId() != null) {
            complaint.setTrip(tripRepository.findById(request.tripId()).orElse(null));
        }
        return complaintRepository.save(complaint);
    }

    @GetMapping("/mine")
    public List<Complaint> myComplaints(@AuthenticationPrincipal String userId) {
        return complaintRepository.findByUserId(UUID.fromString(userId));
    }

    /** For admin/depot manager triage - secure with role check once role-based authorization is added. */
    @GetMapping
    public List<Complaint> allByStatus(@RequestParam(defaultValue = "OPEN") String status) {
        return complaintRepository.findByStatus(status);
    }
}
