package com.CapitalCommute.commute.Controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.Service.FareCalculatorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fares")
@RequiredArgsConstructor
public class FareCalculatorController {

    private final FareCalculatorService fareCalculatorService;

    @GetMapping("/calculate")
    public ResponseEntity<Double> calculateFare(
            @RequestParam double distance,
            @RequestParam double surgeMultiplier) {
        double fare = fareCalculatorService.calculateFare(distance, surgeMultiplier);
        return ResponseEntity.ok(fare);
    }

    @GetMapping("/calculate-by-time")
    public ResponseEntity<Double> calculateFareByTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupTime) {
        double fare = fareCalculatorService.calculateFareByTime(pickupTime);
        return ResponseEntity.ok(fare);
    }

    @GetMapping("/calculate-with-time")
    public ResponseEntity<Double> calculateFareWithTime(
            @RequestParam double distance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupTime) {
        double fare = fareCalculatorService.calculateFare(distance, pickupTime);
        return ResponseEntity.ok(fare);
    }

    @GetMapping("/surge-multiplier")
    public ResponseEntity<Double> getSurgeMultiplier(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        double multiplier = fareCalculatorService.getSurgeMultiplier(time);
        return ResponseEntity.ok(multiplier);
    }

    @GetMapping("/is-peak-hour")
    public ResponseEntity<Boolean> isPeakHour(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        boolean isPeak = fareCalculatorService.isPeakHour(time);
        return ResponseEntity.ok(isPeak);
    }

    @GetMapping("/base-rate")
    public ResponseEntity<Double> getBaseRate() {
        double baseRate = fareCalculatorService.getBaseRate();
        return ResponseEntity.ok(baseRate);
    }
}
