package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Complaint;
import com.tnsmartbus.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintRepository complaintRepository;

    @GetMapping
    public List<Complaint> list(@RequestParam(required = false) String status) {
        return status != null ? complaintRepository.findByStatus(status) : complaintRepository.findAll();
    }

    public record StatusUpdateRequest(String status) {}

    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        complaint.setStatus(request.status());
        return complaintRepository.save(complaint);
    }
}
