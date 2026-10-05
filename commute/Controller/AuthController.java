package com.CapitalCommute.commute.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CapitalCommute.commute.DTO.LoginRequest;
import com.CapitalCommute.commute.DTO.LoginResponse;
import com.CapitalCommute.commute.DTO.RegisterRequest;
import com.CapitalCommute.commute.Service.AuthService;
import com.CapitalCommute.commute.util.CaptchaGenerator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaGenerator captchaGenerator;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        if (response.getRole() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authService.register(registerRequest);

        if (response.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/captcha/generate")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        String captchaText = captchaGenerator.generateCode();
        String captchaImage = captchaGenerator.generateBase64Image(captchaText);
        
        Map<String, String> response = new HashMap<>();
        response.put("captchaText", captchaText);
        response.put("captchaImage", captchaImage);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/captcha/verify")
    public ResponseEntity<Map<String, Boolean>> verifyCaptcha(@RequestBody Map<String, String> request) {
        String userInput = request.get("captchaInput");
        String expectedText = request.get("captchaText");
        
        boolean isValid = captchaGenerator.verify(userInput);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", isValid);
        
        return ResponseEntity.ok(response);
    }
}