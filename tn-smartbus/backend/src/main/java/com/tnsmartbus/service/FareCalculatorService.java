package com.tnsmartbus.service;

import com.tnsmartbus.dto.FareCalculationRequest;
import com.tnsmartbus.dto.FareCalculationResponse;
import org.springframework.stereotype.Service;

/**
 * TNSTC-style slab fare calculation. Rates are illustrative (matching the
 * general shape of real ordinary/express/deluxe slab fares) - swap the
 * constants for the official TNSTC tariff table before going live.
 */
@Service
public class FareCalculatorService {

    private static final double MINIMUM_FARE = 5.0;
    private static final double RATE_PER_KM_ORDINARY = 0.65;
    private static final double RATE_PER_KM_EXPRESS = 0.85;
    private static final double RATE_PER_KM_DELUXE = 1.10;
    private static final double RATE_PER_KM_ULTRA_DELUXE = 1.40;

    private static final double STUDENT_CONCESSION_PCT = 0.50; // 50% off
    private static final double SENIOR_CITIZEN_CONCESSION_PCT = 0.50;

    public FareCalculationResponse calculate(FareCalculationRequest request) {
        double ratePerKm = switch (request.getBusType() == null ? "ORDINARY" : request.getBusType()) {
            case "EXPRESS" -> RATE_PER_KM_EXPRESS;
            case "DELUXE" -> RATE_PER_KM_DELUXE;
            case "ULTRA_DELUXE" -> RATE_PER_KM_ULTRA_DELUXE;
            default -> RATE_PER_KM_ORDINARY;
        };

        double baseFare = Math.max(MINIMUM_FARE, request.getDistanceKm() * ratePerKm);
        baseFare = Math.round(baseFare * 100.0) / 100.0;

        double concessionPct = switch (request.getConcessionType() == null ? "NONE" : request.getConcessionType()) {
            case "STUDENT" -> STUDENT_CONCESSION_PCT;
            case "SENIOR_CITIZEN" -> SENIOR_CITIZEN_CONCESSION_PCT;
            default -> 0.0;
        };

        double discount = Math.round(baseFare * concessionPct * 100.0) / 100.0;
        double finalFare = Math.round((baseFare - discount) * 100.0) / 100.0;

        return new FareCalculationResponse(baseFare, discount, finalFare, request.getBusType());
    }
}
