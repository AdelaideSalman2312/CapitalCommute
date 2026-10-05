package com.CapitalCommute.commute.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.enums.BookingStatus;
import com.CapitalCommute.commute.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking saveBooking(Booking booking) {
        if (bookingRepository.existsByClientIdAndBookingStatus(booking.getClientId(), BookingStatus.CLIENT_REQUESTED)) {
            throw new RuntimeException("Client already has an active booking");
        }
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking getBookingByPaymentId(UUID paymentId) {
        return bookingRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByClientId(UUID clientId) {
        return bookingRepository.findByClientId(clientId);
    }

    public List<Booking> getBookingsByDriverId(UUID driverId) {
        return bookingRepository.findByDriverId(driverId);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByBookingStatus(status);
    }

    public List<Booking> getBookingsByClientAndStatus(UUID clientId, BookingStatus status) {
        return bookingRepository.findByClientIdAndBookingStatus(clientId, status);
    }

    public List<Booking> getBookingsByDriverAndStatus(UUID driverId, BookingStatus status) {
        return bookingRepository.findByDriverIdAndBookingStatus(driverId, status);
    }

    public List<Booking> getBookingsByPickupTimeRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByPickupTimeBetween(start, end);
    }
    public List<Booking> getActiveBookings() {
    return bookingRepository.findByBookingStatus(BookingStatus.TRIP_STARTED);
}

    public Booking updateBooking(Booking booking) {
        bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingRepository.save(booking);
    }

    public void deleteBooking(UUID bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.deleteById(bookingId);
    }
}
