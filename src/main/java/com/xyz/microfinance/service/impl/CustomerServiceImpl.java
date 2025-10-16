package com.xyz.microfinance.service.impl;

import com.xyz.microfinance.dto.request.CustomerRegistrationRequest;
import com.xyz.microfinance.dto.response.CustomerResponse;
import com.xyz.microfinance.dto.response.LoanResponse;
import com.xyz.microfinance.dto.response.PageResponse;
import com.xyz.microfinance.entity.Customer;
import com.xyz.microfinance.entity.Loan;
import com.xyz.microfinance.exception.ResourceNotFoundException;
import com.xyz.microfinance.exception.ValidationException;
import com.xyz.microfinance.repository.CustomerRepository;
import com.xyz.microfinance.repository.LoanRepository;
import com.xyz.microfinance.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private LoanRepository loanRepository;

    @Override
    public CustomerResponse registerCustomer(CustomerRegistrationRequest request) {
        // Check if ID card already exists
        if (customerRepository.existsByIdCard(request.getIdCard())) {
            throw new ValidationException("Customer with ID card " + request.getIdCard() + " already exists");
        }

        // Check if phone number already exists
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ValidationException("Customer with phone number " + request.getPhoneNumber() + " already exists");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setEmploymentStatus(request.getEmploymentStatus());
        customer.setEmployerName(request.getEmployerName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setIdCard(request.getIdCard());
        customer.setAddress(request.getAddress());
        customer.setPhoneNumber(request.getPhoneNumber());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        CustomerResponse response = convertToResponse(customer);
        response = enrichCustomerWithLoanInfo(response);
        response = enrichCustomerWithLoanHistory(response);
        return response;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::convertToResponse)
            .map(this::enrichCustomerWithLoanInfo)
            .collect(Collectors.toList());
    }
    
    @Override
    public PageResponse<CustomerResponse> getAllCustomersPaginated(int page, int size) {
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Customer> customerPage = customerRepository.findAll(pageRequest);
        
        List<CustomerResponse> customerResponses = customerPage.getContent().stream()
            .map(this::convertToResponse)
            .map(this::enrichCustomerWithLoanInfo)
            .collect(Collectors.toList());
            
        return new PageResponse<>(
            customerResponses,
            customerPage.getNumber(),
            customerPage.getSize(),
            customerPage.getTotalElements(),
            customerPage.getTotalPages(),
            customerPage.isLast(),
            customerPage.isFirst()
        );
    }

    @Override
    public List<CustomerResponse> searchCustomers(String searchTerm) {
        return customerRepository.searchCustomers(searchTerm).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Customer getCustomerEntity(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
    
    @Override
    public CustomerResponse enrichCustomerWithLoanInfo(CustomerResponse customerResponse) {
        Object[] loanInfo = loanRepository.getTopCustomersByTotalBorrowed().stream()
                .filter(data -> ((Long) data[0]).equals(customerResponse.getId()))
                .findFirst()
                .orElse(new Object[]{customerResponse.getId(), customerResponse.getName(), 0L, BigDecimal.ZERO});
        
        Long totalLoans = (Long) loanInfo[2];
        BigDecimal totalBorrowed = (BigDecimal) loanInfo[3];
        
        customerResponse.setTotalLoans(totalLoans.intValue());
        customerResponse.setTotalBorrowed(totalBorrowed);
        
        return customerResponse;
    }
    
    @Override
    public CustomerResponse enrichCustomerWithLoanHistory(CustomerResponse customerResponse) {
        List<Loan> loans = loanRepository.findByCustomerIdOrderByDateIssuedDesc(customerResponse.getId());
        List<LoanResponse> loanResponses = loans.stream()
                .map(loan -> {
                    LoanResponse response = new LoanResponse();
                    response.setId(loan.getId());
                    response.setCustomerId(loan.getCustomer().getId());
                    response.setCustomerName(loan.getCustomer().getName());
                    response.setPrincipal(loan.getPrincipal());
                    response.setInterestRate(loan.getInterestRate());
                    response.setTimePeriodYears(loan.getTimePeriodYears());
                    response.setDateIssued(loan.getDateIssued());
                    response.setTotalAmountPayable(loan.getTotalAmountPayable());
                    response.setStatus(loan.getStatus());
                    response.setCreatedAt(loan.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
        
        customerResponse.setLoanHistory(loanResponses);
        return customerResponse;
    }

    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setMaritalStatus(customer.getMaritalStatus());
        response.setEmploymentStatus(customer.getEmploymentStatus());
        response.setEmployerName(customer.getEmployerName());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setIdCard(customer.getIdCard());
        response.setAddress(customer.getAddress());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
    
    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRegistrationRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if ID card already exists for another customer
        if (!customer.getIdCard().equals(request.getIdCard()) && 
            customerRepository.existsByIdCard(request.getIdCard())) {
            throw new ValidationException("Customer with ID card " + request.getIdCard() + " already exists");
        }

        // Check if phone number already exists for another customer
        if (!customer.getPhoneNumber().equals(request.getPhoneNumber()) && 
            customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ValidationException("Customer with phone number " + request.getPhoneNumber() + " already exists");
        }

        // Update customer details
        customer.setName(request.getName());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setEmploymentStatus(request.getEmploymentStatus());
        customer.setEmployerName(request.getEmployerName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setIdCard(request.getIdCard());
        customer.setAddress(request.getAddress());
        customer.setPhoneNumber(request.getPhoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToResponse(updatedCustomer);
    }
    
    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
            
        // Check if customer has any active loans before deletion
        List<Loan> activeLoans = loanRepository.findByCustomerIdAndStatusNot(id, "PAID");
        if (!activeLoans.isEmpty()) {
            throw new ValidationException("Cannot delete customer with active loans");
        }
        
        customerRepository.delete(customer);
    }
}