package com.xyz.microfinance.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "100.00", message = "Principal must be at least 100.00")
    @DecimalMax(value = "100000.00", message = "Principal cannot exceed 100,000.00")
    private BigDecimal principal;

    @NotNull(message = "Date issued is required")
    private LocalDate dateIssued;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }
}