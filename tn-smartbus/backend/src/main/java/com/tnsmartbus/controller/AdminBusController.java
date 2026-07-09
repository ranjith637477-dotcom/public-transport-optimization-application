package com.tnsmartbus.controller;

import com.tnsmartbus.dto.BusRequest;
import com.tnsmartbus.entity.Bus;
import com.tnsmartbus.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/buses")
@RequiredArgsConstructor
public class AdminBusController {

    private final BusRepository busRepository;

    @GetMapping
    public List<Bus> list() {
        return busRepository.findAll();
    }

    @PostMapping
    public Bus create(@RequestBody BusRequest request) {
        Bus bus = new Bus();
        applyRequest(bus, request);
        return busRepository.save(bus);
    }

    @PutMapping("/{id}")
    public Bus update(@PathVariable UUID id, @RequestBody BusRequest request) {
        Bus bus = busRepository.findById(id).orElseThrow();
        applyRequest(bus, request);
        return busRepository.save(bus);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        busRepository.deleteById(id);
    }

    private void applyRequest(Bus bus, BusRequest request) {
        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setBusType(request.getBusType());
        bus.setTotalSeats(request.getTotalSeats());
        bus.setLadiesSeats(request.getLadiesSeats() != null ? request.getLadiesSeats() : 0);
        bus.setSeniorCitizenSeats(request.getSeniorCitizenSeats() != null ? request.getSeniorCitizenSeats() : 0);
        bus.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
    }
}
