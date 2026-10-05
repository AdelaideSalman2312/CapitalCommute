package com.CapitalCommute.commute.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String role;
    private String userId;
    private String nationalId;
    private String message;

    
}
