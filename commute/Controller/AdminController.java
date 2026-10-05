package com.CapitalCommute.commute.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.model.Admin;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.Driver;
import com.CapitalCommute.commute.model.Payment;
import com.CapitalCommute.commute.model.Person;
import com.CapitalCommute.commute.model.Vehicle;
import com.CapitalCommute.commute.model.enums.BookingStatus;
import com.CapitalCommute.commute.model.enums.PaymentStatus;
import com.CapitalCommute.commute.model.enums.VehicleStatus;
import com.CapitalCommute.commute.repository.BookingRepository;
import com.CapitalCommute.commute.repository.PaymentRepository;
import com.CapitalCommute.commute.repository.PersonRepository;
import com.CapitalCommute.commute.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;


    @GetMapping("/users")
    public ResponseEntity<List<Person>> getAllUsers() {
        List<Person> users = personRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{role}")
    public ResponseEntity<List<Person>> getUsersByRole(@PathVariable String role) {
        List<Person> users = personRepository.findAll().stream()
            .filter(person -> {
                if ("CLIENT".equalsIgnoreCase(role)) {
                    return !(person instanceof Driver)  && !(person instanceof Admin);
                } else if ("DRIVER".equalsIgnoreCase(role)) {
                    return person instanceof Driver;
                
                } else if ("ADMIN".equalsIgnoreCase(role)) {
                    return person instanceof Admin;
                }
                return false;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getDrivers() {
        List<Driver> drivers = personRepository.findAll().stream()
            .filter(p -> p instanceof Driver)
            .map(p -> (Driver) p)
            .collect(Collectors.toList());
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
        List<Booking> bookings = bookingRepository.findAll().stream()
            .filter(b -> b.getBookingStatus().name().equalsIgnoreCase(status))
            .collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }

    
   

    @GetMapping("/dashboard/kpi")
    public ResponseEntity<Map<String, Object>> getKPI() {
        Map<String, Object> kpi = new HashMap<>();
        
        long totalUsers = personRepository.count();
        long totalDrivers = personRepository.findAll().stream().filter(p -> p instanceof Driver).count();
        long totalVehicles = vehicleRepository.count();
        long totalBookings = bookingRepository.count();
        long completedBookings = bookingRepository.findAll().stream()
            .filter(b -> b.getBookingStatus() == BookingStatus.TRIP_COMPLETED).count();
       
        
        double totalRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
            .mapToDouble(Payment::getFareAmount).sum();
        
        double completionRate = totalBookings > 0 ? (completedBookings * 100.0 / totalBookings) : 0;
        
        kpi.put("totalRevenue", totalRevenue);
        kpi.put("totalBookings", totalBookings);
        kpi.put("activeRides", bookingRepository.findAll().stream()
            .filter(b -> b.getBookingStatus() == BookingStatus.DRIVER_ASSIGNED || 
                         b.getBookingStatus() == BookingStatus.TRIP_STARTED).count());
        kpi.put("completionRate", Math.round(completionRate * 10) / 10.0);
        kpi.put("avgRating", 4.8);
        kpi.put("totalVehicles", totalVehicles);
        kpi.put("customerSatisfaction", 96.2);
        
        return ResponseEntity.ok(kpi);
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport() {
        Map<String, Object> revenue = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0);
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0);
        
        double dailyRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED && p.getPaymentTime().isAfter(startOfDay))
            .mapToDouble(Payment::getFareAmount).sum();
        
        double weeklyRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED && p.getPaymentTime().isAfter(startOfWeek))
            .mapToDouble(Payment::getFareAmount).sum();
        
        double monthlyRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED && p.getPaymentTime().isAfter(startOfMonth))
            .mapToDouble(Payment::getFareAmount).sum();
        
        double yearlyRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED && p.getPaymentTime().isAfter(startOfYear))
            .mapToDouble(Payment::getFareAmount).sum();
        
        revenue.put("daily", dailyRevenue);
        revenue.put("weekly", weeklyRevenue);
        revenue.put("monthly", monthlyRevenue);
        revenue.put("yearly", yearlyRevenue);
        
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/reports/users")
    public ResponseEntity<Map<String, Object>> getUserReport() {
        Map<String, Object> report = new HashMap<>();
        
        long totalUsers = personRepository.count();
        long newUsersToday = personRepository.findAll().stream()
            .filter(p -> p.getRegistrationDate().toLocalDate().equals(LocalDate.now()))
            .count();
        long activeUsers = personRepository.findAll().stream().filter(Person::isActive).count();
        
        Map<String, Integer> byRole = new HashMap<>();
        byRole.put("CLIENT", (int) personRepository.findAll().stream()
            .filter(p -> !(p instanceof Driver) && !(p instanceof Admin)).count());
        byRole.put("DRIVER", (int) personRepository.findAll().stream().filter(p -> p instanceof Driver).count());
    
        byRole.put("ADMIN", (int) personRepository.findAll().stream().filter(p -> p instanceof Admin).count());
        
        report.put("total", totalUsers);
        report.put("newToday", newUsersToday);
        report.put("active", activeUsers);
        report.put("byRole", byRole);
        
        return ResponseEntity.ok(report);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", personRepository.count());
        stats.put("totalDrivers", personRepository.findAll().stream().filter(p -> p instanceof Driver).count());
        stats.put("totalVehicles", vehicleRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalPayments", paymentRepository.count());
        
        Map<String, Long> bookingStatusCount = new HashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            long count = bookingRepository.findAll().stream()
                .filter(b -> b.getBookingStatus() == status).count();
            bookingStatusCount.put(status.name(), count);
        }
        stats.put("bookingsByStatus", bookingStatusCount);
        
        Map<String, Long> vehicleStatusCount = new HashMap<>();
        for (VehicleStatus status : VehicleStatus.values()) {
            long count = vehicleRepository.findAll().stream()
                .filter(v -> v.getVehicleStatus() == status).count();
            vehicleStatusCount.put(status.name(), count);
        }
        stats.put("vehiclesByStatus", vehicleStatusCount);
        
        return ResponseEntity.ok(stats);
    }
}