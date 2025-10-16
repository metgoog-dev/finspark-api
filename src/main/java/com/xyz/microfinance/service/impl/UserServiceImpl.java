package com.xyz.microfinance.service.impl;

import com.xyz.microfinance.entity.User;
import com.xyz.microfinance.entity.EmailVerification;
import com.xyz.microfinance.dto.request.LoginRequest;
import com.xyz.microfinance.dto.request.RegistrationRequest;
import com.xyz.microfinance.dto.request.VerifyOtpRequest;
import com.xyz.microfinance.dto.response.AuthResponse;
import com.xyz.microfinance.repository.UserRepository;
import com.xyz.microfinance.repository.EmailVerificationRepository;
import com.xyz.microfinance.security.JwtUtil;
import com.xyz.microfinance.security.UserPrincipal;
import com.xyz.microfinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import com.xyz.microfinance.service.MailService;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        log.debug("Authenticating user={}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        AuthResponse response = new AuthResponse(jwt, userPrincipal.getUsername(), userPrincipal.getEmail(), userPrincipal.getRole());
        log.debug("Generated JWT for user={}", response.getUsername());
        return response;
    }

    @Override
    public User createDefaultAgent() {
        // Create a default agent user if none exists
        Optional<User> existingUser = userRepository.findByUsername("agent");
        if (existingUser.isEmpty()) {
            log.info("Creating default agent user");
            User user = new User();
            user.setUsername("agent");
            user.setPassword(passwordEncoder.encode("agent123"));
            user.setEmail("agent@example.com");
            user.setRole("AGENT");
            return userRepository.save(user);
        }
        return existingUser.get();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user");
    }

    @Override
    public void startRegistration(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration rejected: username already taken: {}", request.getUsername());
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected: email already registered: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expires = LocalDateTime.now().plusMinutes(10);

        EmailVerification verification = emailVerificationRepository.findByEmail(request.getEmail())
                .orElse(new EmailVerification());
        verification.setEmail(request.getEmail());
        verification.setUsername(request.getUsername());
        verification.setEncodedPassword(passwordEncoder.encode(request.getPassword()));
        verification.setOtp(otp);
        verification.setExpiresAt(expires);
        verification.setVerified(false);
        emailVerificationRepository.save(verification);
        log.info("Saved OTP for email={}, expiresAt={}", request.getEmail(), expires);

        mailService.sendOtpEmail(request.getEmail(), request.getUsername(), otp);
        log.info("OTP email queued to send for email={}", request.getEmail());
    }

    @Override
    public AuthResponse verifyAndCompleteRegistration(VerifyOtpRequest request) {
        EmailVerification verification = emailVerificationRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No pending verification for email"));

        if (verification.isVerified()) {
            log.info("Email already verified: {}", request.getEmail());
            throw new RuntimeException("Email already verified");
        }
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP expired for email={}", request.getEmail());
            throw new RuntimeException("OTP expired");
        }
        if (!verification.getOtp().equals(request.getOtp())) {
            log.warn("Invalid OTP for email={}", request.getEmail());
            throw new RuntimeException("Invalid OTP");
        }

        // Mark verified and create the user
        verification.setVerified(true);
        emailVerificationRepository.save(verification);
        log.info("Email verification success for email={}", request.getEmail());

        User user = new User();
        user.setUsername(verification.getUsername());
        user.setPassword(verification.getEncodedPassword());
        user.setEmail(verification.getEmail());
        user.setRole("AGENT");
        userRepository.save(user);
        log.info("User created username={}, email={}", user.getUsername(), user.getEmail());

        // Create an Authentication using the newly created user to generate a token
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String jwt = jwtUtil.generateToken(authentication);
        return new AuthResponse(jwt, user.getUsername(), user.getEmail(), user.getRole());
    }
}