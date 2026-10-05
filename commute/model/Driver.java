ense number,vehicle id , drpackage com.CapitalCommute.commute.model;

import java.util.UUID;

import com.CapitalCommute.commute.model.enums.DriverStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "national_id")
public class Driver extends Person {

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_status")
    private DriverStatus driverStatus = DriverStatus.AVAILABLE;

    @Column(name = "driver_rating")
    private double driverRating;

    @PrePersist
    public void generateId() {
        if (driverId == null) {
            driverId = UUID.randomUUID();
        }
    }
}