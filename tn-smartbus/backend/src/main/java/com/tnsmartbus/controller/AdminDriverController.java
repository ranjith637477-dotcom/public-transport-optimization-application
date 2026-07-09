package com.tnsmartbus.controller;

import com.tnsmartbus.entity.Driver;
import com.tnsmartbus.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final DriverRepository driverRepository;

    @GetMapping
    public List<Driver> list() {
        return driverRepository.findAll();
    }
}
