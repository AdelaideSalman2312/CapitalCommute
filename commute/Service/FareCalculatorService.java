package com.CapitalCommute.commute.Service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class FareCalculatorService {

    private static final double BASE_RATE = 500.0;
    private static final double SURGE_MULTIPLIER = 1.5;
    private static final double NORMAL_MULTIPLIER = 1.0;
    private static final double DEFAULT_DISTANCE = 1.0;

    /**
     * Calculate fare based on distance and surge multiplier
     * @param distance Distance in kilometers
     * @param surgeMultiplier Surge pricing multiplier
     * @return Calculated fare
     */
    public double calculateFare(double distance, double surgeMultiplier) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        if (surgeMultiplier <= 0) {
            throw new IllegalArgumentException("Surge multiplier must be greater than 0");
        }
        return distance * BASE_RATE * surgeMultiplier;
    }

    /**
     * Calculate fare based on pickup time (applies surge for peak hours)
     * @param pickupTime The pickup time
     * @return Calculated fare for default distance
     */
    public double calculateFareByTime(LocalDateTime pickupTime) {
        if (pickupTime == null) {
            throw new IllegalArgumentException("Pickup time cannot be null");
        }
        double surgeMultiplier = isPeakHour(pickupTime) ? SURGE_MULTIPLIER : NORMAL_MULTIPLIER;
        return calculateFare(DEFAULT_DISTANCE, surgeMultiplier);
    }

    /**
     * Calculate fare based on distance and pickup time
     * @param distance Distance in kilometers
     * @param pickupTime The pickup time
     * @return Calculated fare with surge pricing if applicable
     */
    public double calculateFare(double distance, LocalDateTime pickupTime) {
        if (pickupTime == null) {
            throw new IllegalArgumentException("Pickup time cannot be null");
        }
        double surgeMultiplier = isPeakHour(pickupTime) ? SURGE_MULTIPLIER : NORMAL_MULTIPLIER;
        return calculateFare(distance, surgeMultiplier);
    }

    /**
     * Get current surge multiplier based on time
     * @param time The time to check
     * @return Surge multiplier
     */
    public double getSurgeMultiplier(LocalDateTime time) {
        return isPeakHour(time) ? SURGE_MULTIPLIER : NORMAL_MULTIPLIER;
    }

    /**
     * Check if given time is during peak hours
     * Peak hours: 7-9 AM and 5-8 PM
     * @param time The time to check
     * @return true if peak hour, false otherwise
     */
    public boolean isPeakHour(LocalDateTime time) {
        if (time == null) {
            return false;
        }
        int hour = time.getHour();
        return (hour >= 7 && hour < 9) || (hour >= 17 && hour < 20);
    }

    /**
     * Get base rate per kilometer
     * @return Base rate
     */
    public double getBaseRate() {
        return BASE_RATE;
    }
}
