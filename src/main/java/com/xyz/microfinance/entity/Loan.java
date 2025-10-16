package com.xyz.microfinance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "100.00", message = "Principal must be at least 100.00")
    @DecimalMax(value = "100000.00", message = "Principal cannot exceed 100,000.00")
    private BigDecimal principal;

    @NotNull(message = "Interest rate is required")
    private BigDecimal interestRate = new BigDecimal("5.00");

    @NotNull(message = "Time period is required")
    private Integer timePeriodYears = 1;

    @NotNull(message = "Date issued is required")
    private LocalDate dateIssued;

    private BigDecimal totalAmountPayable;
    private String status = "ACTIVE"; // ACTIVE, PAID, DEFAULTED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalAmountPayable();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void calculateTotalAmountPayable() {
        if (principal != null && interestRate != null && timePeriodYears != null) {
            BigDecimal simpleInterest = principal
                .multiply(interestRate)
                .multiply(new BigDecimal(timePeriodYears))
                .divide(new BigDecimal("100"));
            this.totalAmountPayable = principal.add(simpleInterest);
        }
    }

    // Constructors, Getters, and Setters
    public Loan() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { 
        this.principal = principal; 
        calculateTotalAmountPayable();
    }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { 
        this.interestRate = interestRate; 
        calculateTotalAmountPayable();
    }
    public Integer getTimePeriodYears() { return timePeriodYears; }
    public void setTimePeriodYears(Integer timePeriodYears) { 
        this.timePeriodYears = timePeriodYears; 
        calculateTotalAmountPayable();
    }
    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }
    public BigDecimal getTotalAmountPayable() { return totalAmountPayable; }
    public void setTotalAmountPayable(BigDecimal totalAmountPayable) { this.totalAmountPayable = totalAmountPayable; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}