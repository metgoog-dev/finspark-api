package com.xyz.microfinance.controller;

import com.xyz.microfinance.dto.request.LoginRequest;
import com.xyz.microfinance.dto.request.RegistrationRequest;
import com.xyz.microfinance.dto.request.VerifyOtpRequest;
import com.xyz.microfinance.dto.response.ApiResponse;
import com.xyz.microfinance.dto.response.AuthResponse;
import com.xyz.microfinance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for user={}", loginRequest.getUsername());
            AuthResponse authResponse = userService.authenticateUser(loginRequest);
            log.info("Login success for user={}", authResponse.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
        } catch (Exception e) {
            log.warn("Login failed for user={}, reason={}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid credentials: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken() {
        return ResponseEntity.ok(ApiResponse.success("Token is valid"));
    }

    @PostMapping("/register/start")
    public ResponseEntity<ApiResponse> startRegistration(@Valid @RequestBody RegistrationRequest request) {
        log.info("Registration start for username={}, email={}", request.getUsername(), request.getEmail());
        userService.startRegistration(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent to email"));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse> verifyRegistration(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Registration verify for email={}", request.getEmail());
        AuthResponse authResponse = userService.verifyAndCompleteRegistration(request);
        return ResponseEntity.ok(ApiResponse.success("Registration complete", authResponse));
    }
}