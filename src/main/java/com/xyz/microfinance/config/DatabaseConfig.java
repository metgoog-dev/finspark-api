package com.xyz.microfinance.config;

import com.xyz.microfinance.entity.User;
import com.xyz.microfinance.repository.UserRepository;
import com.xyz.microfinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.support.RetryTemplate;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DatabaseConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private RetryTemplate retryTemplate;

    @Bean
    public CommandLineRunner initDefaultUser() {
        return args -> {
            retryTemplate.execute(context -> {
                // Create default agent user on application startup
                userService.createDefaultAgent();
                System.out.println("Default agent user initialized");
                return null;
            });
        };
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("system");
    }
}