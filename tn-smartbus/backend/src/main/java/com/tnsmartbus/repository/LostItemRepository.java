package com.tnsmartbus.repository;

import com.tnsmartbus.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostItemRepository extends JpaRepository<LostItem, java.util.UUID> {
    List<LostItem> findByItemType(String itemType);
    List<LostItem> findByStatus(String status);
}
