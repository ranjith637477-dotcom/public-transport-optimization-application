package com.tnsmartbus.controller;

import com.tnsmartbus.dto.FareCalculationRequest;
import com.tnsmartbus.dto.FareCalculationResponse;
import com.tnsmartbus.service.FareCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fare")
@RequiredArgsConstructor
public class FareController {

    private final FareCalculatorService fareCalculatorService;

    @PostMapping("/calculate")
    public FareCalculationResponse calculate(@RequestBody FareCalculationRequest request) {
        return fareCalculatorService.calculate(request);
    }
}
