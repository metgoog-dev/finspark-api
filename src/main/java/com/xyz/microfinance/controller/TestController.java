package com.xyz.microfinance.controller;

import com.xyz.microfinance.dto.response.ApiResponse;
import com.xyz.microfinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xyz.microfinance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("API is running"));
    }

    @GetMapping("/create-default-user")
    public ResponseEntity<ApiResponse> createDefaultUser() {
        try {
            userService.createDefaultAgent();
            return ResponseEntity.ok(ApiResponse.success("Default user created/verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create default user: " + e.getMessage()));
        }
    }

    @GetMapping("/verify-password")
    public ResponseEntity<ApiResponse> verifyPassword() {
        try {
            var user = userRepository.findByUsername("agent").orElseThrow();

            boolean matches = passwordEncoder.matches("agent123", user.getPassword());

            Map<String, Object> result = new HashMap<>();
            result.put("storedPassword", user.getPassword());
            result.put("passwordMatches", matches);
            result.put("passwordLength", user.getPassword().length());

            return ResponseEntity.ok(ApiResponse.success("Password verification result", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password verification failed: " + e.getMessage()));
        }
    }
}