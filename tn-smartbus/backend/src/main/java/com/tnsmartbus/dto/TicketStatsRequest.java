package com.tnsmartbus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketStatsRequest {
    private int ticketsIssued;
    private BigDecimal totalRevenue;
}
