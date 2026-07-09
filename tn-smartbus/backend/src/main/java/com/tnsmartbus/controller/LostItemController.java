package com.tnsmartbus.controller;

import com.tnsmartbus.entity.LostItem;
import com.tnsmartbus.repository.LostItemRepository;
import com.tnsmartbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lost-and-found")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;

    public record LostItemRequest(String itemDescription, String itemType, String contactInfo) {}

    @PostMapping
    public LostItem report(@AuthenticationPrincipal String userId, @RequestBody LostItemRequest request) {
        LostItem item = new LostItem();
        item.setUser(userRepository.findById(UUID.fromString(userId)).orElseThrow());
        item.setItemDescription(request.itemDescription());
        item.setItemType(request.itemType() != null ? request.itemType() : "LOST");
        item.setContactInfo(request.contactInfo());
        return lostItemRepository.save(item);
    }

    @GetMapping
    public List<LostItem> search(@RequestParam(defaultValue = "LOST") String itemType) {
        return lostItemRepository.findByItemType(itemType);
    }
}
