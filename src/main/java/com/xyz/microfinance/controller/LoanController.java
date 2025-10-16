package com.xyz.microfinance.controller;

import com.xyz.microfinance.dto.request.LoanRequest;
import com.xyz.microfinance.dto.response.ApiResponse;
import com.xyz.microfinance.dto.response.LoanResponse;
import com.xyz.microfinance.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    @Operation(summary = "Create a new loan")
    public ResponseEntity<ApiResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        LoanResponse loanResponse = loanService.createLoan(request);
        return ResponseEntity.ok(ApiResponse.success("Loan created successfully", loanResponse));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get loans by customer ID")
    public ResponseEntity<ApiResponse> getLoansByCustomerId(@PathVariable Long customerId) {
        List<LoanResponse> loans = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
    }

    @GetMapping
    @Operation(summary = "List all loans (paginated)")
    public ResponseEntity<ApiResponse> getAllLoans(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateIssued").descending());
        Page<LoanResponse> loanPage = loanService.getAllLoansPaginated(pageable);
        return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loanPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID")
    public ResponseEntity<ApiResponse> getLoanById(@PathVariable Long id) {
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.success("Loan retrieved successfully", loan));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing loan")
    public ResponseEntity<ApiResponse> updateLoan(@PathVariable Long id, @Valid @RequestBody LoanRequest request) {
        LoanResponse loanResponse = loanService.updateLoan(id, request);
        return ResponseEntity.ok(ApiResponse.success("Loan updated successfully", loanResponse));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan")
    public ResponseEntity<ApiResponse> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.ok(ApiResponse.success("Loan deleted successfully", null));
    }
}