package com.tnsmartbus.controller;

import com.tnsmartbus.dto.JourneyPlanOption;
import com.tnsmartbus.dto.JourneyPlanRequest;
import com.tnsmartbus.service.JourneyPlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/journey")
@RequiredArgsConstructor
public class JourneyPlannerController {

    private final JourneyPlannerService journeyPlannerService;

    @PostMapping("/plan")
    public List<JourneyPlanOption> plan(@RequestBody JourneyPlanRequest request) {
        return journeyPlannerService.planJourney(request);
    }
}
