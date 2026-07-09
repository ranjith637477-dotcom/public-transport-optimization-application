package com.tnsmartbus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalBuses;
    private long runningBuses;
    private long delayedBuses;
    private long completedTripsToday;
    private long openComplaints;
    private double averageDelayMinutes;
}
