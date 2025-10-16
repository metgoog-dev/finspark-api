package com.xyz.microfinance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer timePeriodYears;
    private LocalDate dateIssued;
    private BigDecimal totalAmountPayable;
    private String status;
    private LocalDateTime createdAt;

    // Constructors
    public LoanResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getTimePeriodYears() { return timePeriodYears; }
    public void setTimePeriodYears(Integer timePeriodYears) { this.timePeriodYears = timePeriodYears; }
    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }
    public BigDecimal getTotalAmountPayable() { return totalAmountPayable; }
    public void setTotalAmountPayable(BigDecimal totalAmountPayable) { this.totalAmountPayable = totalAmountPayable; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}