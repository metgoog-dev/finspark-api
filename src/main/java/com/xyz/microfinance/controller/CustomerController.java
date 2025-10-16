package com.xyz.microfinance.controller;

import com.xyz.microfinance.dto.request.CustomerRegistrationRequest;
import com.xyz.microfinance.dto.response.ApiResponse;
import com.xyz.microfinance.dto.response.CustomerResponse;
import com.xyz.microfinance.dto.response.PageResponse;
import com.xyz.microfinance.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerResponse customerResponse = customerService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", customerResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponse customerResponse = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customerResponse));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    public ResponseEntity<ApiResponse> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CustomerResponse> customers = customerService.getAllCustomersPaginated(page, size);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by query")
    public ResponseEntity<ApiResponse> searchCustomers(@RequestParam String q) {
        List<CustomerResponse> customers = customerService.searchCustomers(q);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", customers));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerResponse customerResponse = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customerResponse));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
}