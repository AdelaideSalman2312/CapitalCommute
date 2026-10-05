package com.CapitalCommute.commute.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByClientId(UUID clientId);

    List<Booking> findByDriverId(UUID driverId);

    List<Booking> findByVehicleId(UUID vehicleId);

    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

    List<Booking> findByClientIdAndBookingStatus(UUID clientId, BookingStatus bookingStatus);

    List<Booking> findByDriverIdAndBookingStatus(UUID driverId, BookingStatus bookingStatus);

    Optional<Booking> findByPaymentId(UUID paymentId);

    List<Booking> findByPickupTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByClientIdAndBookingStatus(UUID clientId, BookingStatus bookingStatus);
}