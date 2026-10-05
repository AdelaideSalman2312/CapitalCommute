package com.CapitalCommute.commute.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.CapitalCommute.commute.model.enums.BookingStatus;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {
 @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    private UUID bookingId;

   

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "driver_id")
    private UUID driverId;
   

    @Column(name = "vehicle_id")
    private UUID vehicleId;





    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @Column(name = "dropoff_location")
    private String dropoffLocation;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;



    @Column(name = "dropoff_time", updatable = false)
    private LocalDateTime dropoffTime;


     @CreationTimestamp
    @Column(name = "booking_time", updatable = false)
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus bookingStatus = BookingStatus.CLIENT_REQUESTED;



    @Column(name = "fare_amount")
    private double fareAmount = 0.0;

    
    
    @Column(name = "payment_id")
    private UUID paymentId;



    @Column(name="distance")
    private double distance;

  
}
