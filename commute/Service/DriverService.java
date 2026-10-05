package com.CapitalCommute.commute.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CapitalCommute.commute.model.Driver;
import com.CapitalCommute.commute.model.enums.DriverStatus;
import com.CapitalCommute.commute.repository.DriverRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public Driver saveDriver(Driver driver) {
        if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber())) {
            throw new RuntimeException("License number already registered");
        }
        if (driverRepository.existsByVehicleId(driver.getVehicleId())) {
            throw new RuntimeException("Vehicle already assigned to another driver");
        }
        return driverRepository.save(driver);
    }

    public Driver getDriverByNationalId(String nationalId) {
        return driverRepository.findById(nationalId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public Driver getDriverByDriverId(UUID driverId) {
        return driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public Driver getDriverByLicenseNumber(String licenseNumber) {
        return driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByDriverStatus(status);
    }

    public List<Driver> getAvailableDriversWithVehicle() {
        return driverRepository.findByDriverStatusAndVehicleIdIsNotNull(DriverStatus.AVAILABLE);
    }

    public List<Driver> getTopRatedDrivers(double minRating) {
        return driverRepository.findByDriverRatingGreaterThanEqual(minRating);
    }

    public Driver updateDriver(String nationalId, Driver updatedDriver) {
        Driver existingDriver = driverRepository.findById(nationalId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        // Update Person fields
        existingDriver.setFirstName(updatedDriver.getFirstName());
        existingDriver.setMiddleName(updatedDriver.getMiddleName());
        existingDriver.setLastName(updatedDriver.getLastName());
        existingDriver.setEmail(updatedDriver.getEmail());
        existingDriver.setPhoneNumber(updatedDriver.getPhoneNumber());
        existingDriver.setResidence(updatedDriver.getResidence());
        existingDriver.setDateOfBirth(updatedDriver.getDateOfBirth());
        existingDriver.setGender(updatedDriver.getGender());
        existingDriver.setPassportNumber(updatedDriver.getPassportNumber());
        existingDriver.setActive(updatedDriver.isActive());
        
        // Update Driver-specific fields
        existingDriver.setLicenseNumber(updatedDriver.getLicenseNumber());
        existingDriver.setVehicleId(updatedDriver.getVehicleId());
        existingDriver.setDriverStatus(updatedDriver.getDriverStatus());
        existingDriver.setDriverRating(updatedDriver.getDriverRating());
        
        return driverRepository.save(existingDriver);
    }

    public void deleteDriver(String nationalId) {
        driverRepository.findById(nationalId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driverRepository.deleteById(nationalId);
    }
}