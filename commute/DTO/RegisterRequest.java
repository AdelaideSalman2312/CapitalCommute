
package com.CapitalCommute.commute.DTO;

import java.time.LocalDate;

import com.CapitalCommute.commute.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    private String nationalId;
    private String firstName;
    private String middleName;
    private String lastName;
      private String userName;
    private LocalDate dateOfBirth;
    private String residence;
    private String passportNumber;

    @JsonProperty("isMinor")
    private Boolean isMinor;

    private String email;
    private String phoneNumber;
    private Gender gender;
    private String password;
    private String confirmPassword;
    private String role;
    private String licenseNumber;
    private String specialization;
    private String captchaSessionId;
    private int captchaAnswer;
    

    
}
