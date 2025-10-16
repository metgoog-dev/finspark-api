package com.xyz.microfinance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerResponse {
    private Long id;
    private String name;
    private String maritalStatus;
    private String employmentStatus;
    private String employerName;
    private LocalDate dateOfBirth;
    private String idCard;
    private String address;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Integer totalLoans;
    private BigDecimal totalBorrowed;
    private List<LoanResponse> loanHistory;

    // Constructors
    public CustomerResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getTotalLoans() { return totalLoans; }
    public void setTotalLoans(Integer totalLoans) { this.totalLoans = totalLoans; }
    public BigDecimal getTotalBorrowed() { return totalBorrowed; }
    public void setTotalBorrowed(BigDecimal totalBorrowed) { this.totalBorrowed = totalBorrowed; }
    public List<LoanResponse> getLoanHistory() { return loanHistory; }
    public void setLoanHistory(List<LoanResponse> loanHistory) { this.loanHistory = loanHistory; }
}