package com.CapitalCommute.commute.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.Service.BookingService;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.enums.BookingStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking savedBooking = bookingService.saveBooking(booking);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Booking> getBookingByPaymentId(@PathVariable UUID paymentId) {
        Booking booking = bookingService.getBookingByPaymentId(paymentId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Booking>> getBookingsByClientId(@PathVariable UUID clientId) {
        List<Booking> bookings = bookingService.getBookingsByClientId(clientId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Booking>> getBookingsByDriverId(@PathVariable UUID driverId) {
        List<Booking> bookings = bookingService.getBookingsByDriverId(driverId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/client/{clientId}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByClientAndStatus(
            @PathVariable UUID clientId,
            @PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByClientAndStatus(clientId, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/driver/{driverId}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByDriverAndStatus(
            @PathVariable UUID driverId,
            @PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByDriverAndStatus(driverId, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/pickup-time-range")
    public ResponseEntity<List<Booking>> getBookingsByPickupTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Booking> bookings = bookingService.getBookingsByPickupTimeRange(start, end);
        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/active")
public ResponseEntity<List<Booking>> getActiveBookings() {
    return ResponseEntity.ok(bookingService.getActiveBookings());
}

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody Booking booking) {
        booking.setBookingId(bookingId);
        Booking updatedBooking = bookingService.updateBooking(booking);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
