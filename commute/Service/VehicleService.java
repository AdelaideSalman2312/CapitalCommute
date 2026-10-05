package com.CapitalCommute.commute.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CapitalCommute.commute.model.Vehicle;
import com.CapitalCommute.commute.model.enums.VehicleStatus;
import com.CapitalCommute.commute.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("License plate already registered");
        }
        if (vehicleRepository.existsByInsuranceNumber(vehicle.getInsuranceNumber())) {
            throw new RuntimeException("Insurance number already registered");
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleById(UUID vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByVehicleStatus(status);
    }

    public List<Vehicle> getVehiclesByMakeAndModel(String make, String model) {
        return vehicleRepository.findByMakeAndModel(make, model);
    }

    public List<Vehicle> getVehiclesByYear(int year) {
        return vehicleRepository.findByYear(year);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        vehicleRepository.findById(vehicle.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(UUID vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicleRepository.deleteById(vehicleId);
    }
}
