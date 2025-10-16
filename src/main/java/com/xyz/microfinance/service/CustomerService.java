package com.xyz.microfinance.service;

import com.xyz.microfinance.dto.request.CustomerRegistrationRequest;
import com.xyz.microfinance.dto.response.CustomerResponse;
import com.xyz.microfinance.dto.response.PageResponse;
import com.xyz.microfinance.entity.Customer;

import java.util.List;

public interface CustomerService {
    CustomerResponse registerCustomer(CustomerRegistrationRequest request);
    CustomerResponse getCustomerById(Long id);
    List<CustomerResponse> getAllCustomers();
    PageResponse<CustomerResponse> getAllCustomersPaginated(int page, int size);
    List<CustomerResponse> searchCustomers(String searchTerm);
    Customer getCustomerEntity(Long id);
    CustomerResponse enrichCustomerWithLoanInfo(CustomerResponse customerResponse);
    CustomerResponse enrichCustomerWithLoanHistory(CustomerResponse customerResponse);
    CustomerResponse updateCustomer(Long id, CustomerRegistrationRequest request);
    void deleteCustomer(Long id);
}