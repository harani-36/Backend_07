package com.fare.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DynamicPricingService {

    /**
     * Calculates price multiplier based on seat occupancy and days until journey.
     * Higher occupancy and closer journey date = higher multiplier.
     */
    public double calculateMultiplier(int availableSeats, int totalSeats, int daysUntilJourney) {
        double occupancyRate = totalSeats > 0 ? (double)(totalSeats - availableSeats) / totalSeats : 0;
        double multiplier = 1.0;

        // Occupancy-based surge: fewer available seats → higher price
        if (occupancyRate >= 0.9)      multiplier += 0.5;
        else if (occupancyRate >= 0.8) multiplier += 0.3;
        else if (occupancyRate >= 0.7) multiplier += 0.2;
        else if (occupancyRate >= 0.5) multiplier += 0.1;

        // Time-based surge: closer to journey date → higher price
        if (daysUntilJourney <= 1)      multiplier += 0.3;
        else if (daysUntilJourney <= 3) multiplier += 0.2;
        else if (daysUntilJourney <= 7) multiplier += 0.1;

        // Early bird discount: booked 30+ days in advance
        if (daysUntilJourney > 30) multiplier -= 0.1;

        double result = Math.max(0.8, multiplier);
        log.debug("Multiplier: occupancy={}%, days={}, result={}", Math.round(occupancyRate * 100), daysUntilJourney, result);
        return result;
    }

    public String getSurgeLevel(double multiplier) {
        if (multiplier >= 1.4) return "Very High Demand";
        if (multiplier >= 1.2) return "High Demand";
        if (multiplier >= 1.1) return "Moderate Demand";
        if (multiplier < 1.0)  return "Early Bird Discount";
        return "Normal Pricing";
    }
}
