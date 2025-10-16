package com.xyz.microfinance.service;

import com.xyz.microfinance.dto.request.LoanRequest;
import com.xyz.microfinance.dto.response.LoanResponse;
import com.xyz.microfinance.dto.response.DashboardStatsResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanRequest request);

    List<LoanResponse> getLoansByCustomerId(Long customerId);

    List<LoanResponse> getAllLoans();

    LoanResponse getLoanById(Long id);

    DashboardStatsResponse getDashboardStats();
    
    LoanResponse updateLoan(Long id, LoanRequest request);
    
    void deleteLoan(Long id);

    Page<LoanResponse> getAllLoansPaginated(Pageable pageable);
}