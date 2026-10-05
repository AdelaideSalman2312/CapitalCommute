package com.CapitalCommute.commute.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.CapitalCommute.commute.model.enums.RideStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ride_id")
    private UUID rideId;
    
    @Column(name = "client_id", nullable = false)
    private UUID clientId;
    
    @Column(name = "driver_id")
    private UUID driverId;
    
    @Column(name = "vehicle_id")
    private UUID vehicleId;
    
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;
    
    @Column(name = "dropoff_location", nullable = false)
    private String dropoffLocation;
    
    @Column(name = "fare")
    private double fare;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ride_status")
    private RideStatus rideStatus = RideStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "ride_date", updatable = false)
    private LocalDateTime rideDate;
}
