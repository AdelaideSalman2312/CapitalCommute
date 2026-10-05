package com.CapitalCommute.commute.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CapitalCommute.commute.model.Vehicle;
import com.CapitalCommute.commute.model.enums.VehicleStatus;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByInsuranceNumber(String insuranceNumber);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByInsuranceNumber(String insuranceNumber);

    List<Vehicle> findByVehicleStatus(VehicleStatus vehicleStatus);

    List<Vehicle> findByMakeAndModel(String make, String model);

    List<Vehicle> findByYear(int year);
}