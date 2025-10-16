package com.xyz.microfinance.dto.response;

import java.math.BigDecimal;

public class TopCustomerResponse {
    private Long customerId;
    private String customerName;
    private int totalLoans;
    private BigDecimal totalBorrowed;

    public TopCustomerResponse() {}

    public TopCustomerResponse(Long customerId, String customerName, int totalLoans, BigDecimal totalBorrowed) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalLoans = totalLoans;
        this.totalBorrowed = totalBorrowed;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getTotalLoans() { return totalLoans; }
    public void setTotalLoans(int totalLoans) { this.totalLoans = totalLoans; }

    public BigDecimal getTotalBorrowed() { return totalBorrowed; }
    public void setTotalBorrowed(BigDecimal totalBorrowed) { this.totalBorrowed = totalBorrowed; }
}
