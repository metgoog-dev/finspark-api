package com.xyz.microfinance.controller;

import com.xyz.microfinance.dto.response.ApiResponse;
import com.xyz.microfinance.dto.response.DashboardStatsResponse;
import com.xyz.microfinance.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        DashboardStatsResponse stats = loanService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
    }
}
