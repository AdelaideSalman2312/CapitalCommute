package com.CapitalCommute.commute.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class DebugController {
    
    @PostMapping("/debug")
    public ResponseEntity<String> debug(@RequestBody String body) {
        System.out.println("Debug request received: " + body);
        return ResponseEntity.ok("Received");
    }
}