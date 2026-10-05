package com.CapitalCommute.commute.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CapitalCommute.commute.model.Driver;
import com.CapitalCommute.commute.model.enums.DriverStatus;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    Optional<Driver> findByDriverId(UUID driverId);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    List<Driver> findByDriverStatus(DriverStatus status);
    List<Driver> findByDriverStatusAndVehicleIdIsNotNull(DriverStatus status);
    List<Driver> findByDriverRatingGreaterThanEqual(double rating);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVehicleId(UUID vehicleId);
}