package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Notification;
import com.tnsmartbus.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> myNotifications(@AuthenticationPrincipal String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(UUID.fromString(userId));
    }

    @PutMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setIsRead(true);
        return notificationRepository.save(n);
    }
}
