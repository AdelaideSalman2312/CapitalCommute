package com.CapitalCommute.commute.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CapitalCommute.commute.model.Payment;
import com.CapitalCommute.commute.model.enums.PaymentMethod;
import com.CapitalCommute.commute.model.enums.PaymentStatus;
import com.CapitalCommute.commute.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        if (paymentRepository.existsByBookingId(payment.getBookingId())) {
            throw new RuntimeException("Payment already exists for this booking");
        }
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment getPaymentByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    public List<Payment> getPaymentsByMethodAndStatus(PaymentMethod method, PaymentStatus status) {
        return paymentRepository.findByPaymentMethodAndPaymentStatus(method, status);
    }

    public List<Payment> getPaymentsByMinAmount(double amount) {
        return paymentRepository.findByFareAmountGreaterThanEqual(amount);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentTimeBetween(start, end);
    }

    public Payment updatePayment(Payment payment) {
        paymentRepository.findById(payment.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentRepository.save(payment);
    }

    public void deletePayment(UUID paymentId) {
        paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.deleteById(paymentId);
    }
}