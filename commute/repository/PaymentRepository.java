package com.CapitalCommute.commute.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CapitalCommute.commute.model.Payment;
import com.CapitalCommute.commute.model.enums.PaymentMethod;
import com.CapitalCommute.commute.model.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Payment> findByPaymentMethodAndPaymentStatus(PaymentMethod paymentMethod, PaymentStatus paymentStatus);

    List<Payment> findByFareAmountGreaterThanEqual(double amount);

    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByBookingId(UUID bookingId);
}