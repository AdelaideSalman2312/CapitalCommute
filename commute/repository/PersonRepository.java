package com.CapitalCommute.commute.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CapitalCommute.commute.model.Person;
import com.CapitalCommute.commute.model.enums.Gender;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {

    Optional<Person> findByEmail(String email);

    Optional<Person> findByPhoneNumber(String phoneNumber);

    Optional<Person> findByPassportNumber(String passportNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNationalId(String nationalId);

    List<Person> findByIsMinor(boolean isMinor);

    List<Person> findByGender(Gender gender);

    List<Person> findByIsActive(boolean isActive);

    List<Person> findByDateOfBirthBefore(LocalDate date);
}
