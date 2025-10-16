package com.xyz.microfinance.service;

public interface MailService {
    void sendOtpEmail(String toEmail, String username, String otp);
}

