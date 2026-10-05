package com.CapitalCommute.commute.Service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CapitalCommute.commute.DTO.LoginRequest;
import com.CapitalCommute.commute.DTO.LoginResponse;
import com.CapitalCommute.commute.DTO.RegisterRequest;
import com.CapitalCommute.commute.model.Client;
import com.CapitalCommute.commute.model.Driver;
import com.CapitalCommute.commute.model.Person;
import com.CapitalCommute.commute.repository.ClientRepository;
import com.CapitalCommute.commute.repository.DriverRepository;
import com.CapitalCommute.commute.repository.PersonRepository;
import com.CapitalCommute.commute.util.CaptchaGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonRepository personRepository;
    private final ClientRepository clientRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaGenerator captchaGenerator;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Person person = personRepository.findByEmail(request.getEmail())
            .orElse(null);

        if (person == null) {
            return new LoginResponse(null, null, null, "Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), person.getPassword())) {
            return new LoginResponse(null, null, null, "Invalid email or password");
        }

        if (!person.isActive()) {
            return new LoginResponse(null, null, null, "Account is deactivated");
        }

        String role = determineRole(person);
        String userId = getUserId(person, role);

        return new LoginResponse(role, userId, person.getNationalId(), "Login successful");
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (personRepository.existsByEmail(request.getEmail())) {
            return new LoginResponse(null, null, null, "Email already exists");
        }
        if (personRepository.existsByNationalId(request.getNationalId())) {
            return new LoginResponse(null, null, null, "National ID already exists");
        }
        if (personRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return new LoginResponse(null, null, null, "Phone number already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Person savedPerson;
        String role;
        String userId;

        switch (request.getRole().toUpperCase()) {
            case "CLIENT":
                Client client = new Client();
                setPersonFields(client, request, hashedPassword);
                savedPerson = clientRepository.save(client);
                role = "CLIENT";
                userId = ((Client) savedPerson).getClientId().toString();
                break;

            case "DRIVER":
                Driver driver = new Driver();
                setPersonFields(driver, request, hashedPassword);
                if (request.getLicenseNumber() != null && !request.getLicenseNumber().isEmpty()) {
                    driver.setLicenseNumber(request.getLicenseNumber());
                }
                driver.setDriverStatus(com.CapitalCommute.commute.model.enums.DriverStatus.AVAILABLE);
                driver.setDriverRating(0.0);
                savedPerson = driverRepository.save(driver);
                role = "DRIVER";
                userId = ((Driver) savedPerson).getDriverId().toString();
                break;


            default:
                return new LoginResponse(null, null, null, "Invalid role");
        }

        return new LoginResponse(role, userId, savedPerson.getNationalId(), "Registration successful");
    }

    private void setPersonFields(Person person, RegisterRequest request, String hashedPassword) {
        person.setNationalId(request.getNationalId());
        person.setFirstName(request.getFirstName());
        person.setMiddleName(request.getMiddleName());
        person.setLastName(request.getLastName());
        person.setDateOfBirth(request.getDateOfBirth());
        person.setResidence(request.getResidence());
        person.setPassportNumber(request.getPassportNumber());
        person.setEmail(request.getEmail());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setGender(request.getGender());
        person.setPassword(hashedPassword);
        person.setMinor(Boolean.TRUE.equals(request.getIsMinor()));
        person.setActive(true);
        person.setRegistrationDate(LocalDateTime.now());
    }

    private String determineRole(Person person) {
        if (person instanceof Client) return "CLIENT";
        if (person instanceof Driver) return "DRIVER";
        return "ADMIN";
    }

    private String getUserId(Person person, String role) {
        switch (role) {
            case "CLIENT":
                return ((Client) person).getClientId() != null
                    ? ((Client) person).getClientId().toString() : null;
            case "DRIVER":
                return ((Driver) person).getDriverId() != null
                    ? ((Driver) person).getDriverId().toString() : null;
            
            default:
                return person.getNationalId();
        }
    }
}