package com.xyz.microfinance.service;

import com.xyz.microfinance.entity.User;
import com.xyz.microfinance.dto.request.LoginRequest;
import com.xyz.microfinance.dto.request.RegistrationRequest;
import com.xyz.microfinance.dto.request.VerifyOtpRequest;
import com.xyz.microfinance.dto.response.AuthResponse;

public interface UserService {
    AuthResponse authenticateUser(LoginRequest loginRequest);
    User createDefaultAgent();
    User getCurrentUser();
    void startRegistration(RegistrationRequest request);
    AuthResponse verifyAndCompleteRegistration(VerifyOtpRequest request);
}