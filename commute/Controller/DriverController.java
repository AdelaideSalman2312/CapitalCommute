package com.CapitalCommute.commute.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.Service.DriverService;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.Driver;
import com.CapitalCommute.commute.model.enums.DriverStatus;
import com.CapitalCommute.commute.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final BookingRepository bookingRepository;

    @PostMapping("/drivers")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverService.saveDriver(driver);
        return new ResponseEntity<>(savedDriver, HttpStatus.CREATED);
    }

    @GetMapping("/drivers/national-id/{nationalId}")
    public ResponseEntity<Driver> getDriverByNationalId(@PathVariable String nationalId) {
        Driver driver = driverService.getDriverByNationalId(nationalId);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/drivers/driver-id/{driverId}")
    public ResponseEntity<Driver> getDriverByDriverId(@PathVariable UUID driverId) {
        Driver driver = driverService.getDriverByDriverId(driverId);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/drivers/license/{licenseNumber}")
    public ResponseEntity<Driver> getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        Driver driver = driverService.getDriverByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/status/{status}")
    public ResponseEntity<List<Driver>> getDriversByStatus(@PathVariable DriverStatus status) {
        List<Driver> drivers = driverService.getDriversByStatus(status);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<Driver>> getAvailableDriversWithVehicle() {
        List<Driver> drivers = driverService.getAvailableDriversWithVehicle();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/top-rated")
    public ResponseEntity<List<Driver>> getTopRatedDrivers(@RequestParam double minRating) {
        List<Driver> drivers = driverService.getTopRatedDrivers(minRating);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/drivers/{nationalId}")
    public ResponseEntity<Driver> updateDriver(
            @PathVariable String nationalId,
            @RequestBody Driver driver) {
        Driver updatedDriver = driverService.updateDriver(nationalId, driver);
        return ResponseEntity.ok(updatedDriver);
    }

    @DeleteMapping("/drivers/{nationalId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String nationalId) {
        driverService.deleteDriver(nationalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rides/driver/{driverId}")
    public ResponseEntity<List<Booking>> getRidesByDriver(@PathVariable UUID driverId) {
        List<Booking> rides = bookingRepository.findAll().stream()
            .filter(booking -> booking.getDriverId() != null && booking.getDriverId().equals(driverId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/rides/available")
    public ResponseEntity<List<Booking>> getAvailableRides() {
        List<Booking> availableRides = bookingRepository.findAll().stream()
            .filter(booking -> booking.getDriverId() == null)
            .filter(booking -> booking.getBookingStatus().name().equals("CLIENT_REQUESTED"))
            .collect(Collectors.toList());
        return ResponseEntity.ok(availableRides);
    }

    @PutMapping("/rides/{bookingId}/accept")
    public ResponseEntity<Booking> acceptRide(@PathVariable UUID bookingId, @RequestBody Map<String, UUID> request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        UUID driverId = request.get("driverId");
        booking.setDriverId(driverId);
        booking.setBookingStatus(com.CapitalCommute.commute.model.enums.BookingStatus.DRIVER_ASSIGNED);
        
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/rides/{bookingId}/status")
    public ResponseEntity<Booking> updateRideStatus(@PathVariable UUID bookingId, @RequestBody Map<String, String> request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        String status = request.get("status");
        booking.setBookingStatus(com.CapitalCommute.commute.model.enums.BookingStatus.valueOf(status));
        
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(saved);
    }
}