package com.xyz.microfinance.service.impl;

import com.xyz.microfinance.dto.request.LoanRequest;
import com.xyz.microfinance.dto.response.LoanResponse;
import com.xyz.microfinance.dto.response.DashboardStatsResponse;
import com.xyz.microfinance.dto.response.TopCustomerResponse;
import com.xyz.microfinance.dto.response.ChartDataResponse;
import com.xyz.microfinance.entity.Customer;
import com.xyz.microfinance.entity.Loan;
import com.xyz.microfinance.exception.ResourceNotFoundException;
import com.xyz.microfinance.exception.ValidationException;
import com.xyz.microfinance.repository.LoanRepository;
import com.xyz.microfinance.repository.CustomerRepository;
import com.xyz.microfinance.service.CustomerService;
import com.xyz.microfinance.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Override
    public LoanResponse createLoan(LoanRequest request) {
        Customer customer = customerService.getCustomerEntity(request.getCustomerId());

        List<Loan> activeLoans = loanRepository.findActiveLoansByCustomerId(request.getCustomerId());
        if (!activeLoans.isEmpty()) {
            throw new ValidationException(
                    "Customer has existing active loans. Please clear existing loans before issuing a new one.");
        }
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setPrincipal(request.getPrincipal());
        loan.setDateIssued(request.getDateIssued());
        loan.calculateTotalAmountPayable();

        Loan savedLoan = loanRepository.save(loan);
        return convertToResponse(savedLoan);
    }

    @Override
    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        customerService.getCustomerEntity(customerId);

        return loanRepository.findByCustomerIdOrderByDateIssuedDesc(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LoanResponse> getAllLoansPaginated(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        return convertToResponse(loan);
    }
    
    @Override
    public LoanResponse updateLoan(Long id, LoanRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        
        if (!loan.getCustomer().getId().equals(request.getCustomerId())) {
            Customer customer = customerService.getCustomerEntity(request.getCustomerId());
            loan.setCustomer(customer);
        }
        loan.setPrincipal(request.getPrincipal());
        loan.setDateIssued(request.getDateIssued());
        loan.calculateTotalAmountPayable();
        
        Loan updatedLoan = loanRepository.save(loan);
        return convertToResponse(updatedLoan);
    }
    
    @Override
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        
        loanRepository.delete(loan);
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalCustomers(customerRepository.count());
        stats.setTotalLoans(loanRepository.count());
        stats.setActiveLoans(loanRepository.findByStatusOrderByDateIssuedDesc("ACTIVE").size());
        stats.setPendingLoans(loanRepository.findByStatusOrderByDateIssuedDesc("PENDING").size());
        Double disbursed = loanRepository.getTotalActiveLoanAmount();
        stats.setTotalDisbursed(disbursed != null ? BigDecimal.valueOf(disbursed) : BigDecimal.ZERO);
        
        List<Loan> recentLoans = loanRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "dateIssued"));
        List<LoanResponse> loanResponses = recentLoans.stream()
            .limit(5)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        stats.setRecentLoans(loanResponses);
        
        List<Object[]> topCustomersData = loanRepository.getTopCustomersByTotalBorrowed();
        List<TopCustomerResponse> topCustomers = topCustomersData.stream()
            .limit(3)
            .map(this::convertToTopCustomerResponse)
            .collect(Collectors.toList());
        stats.setTopCustomers(topCustomers);
        LocalDate startDate = LocalDate.now().minusDays(6);
        List<Object[]> chartDataRaw = loanRepository.getLoanActivityForLast7Days(startDate);
        List<ChartDataResponse> chartData = generateChartData(startDate, chartDataRaw);
        stats.setChartData(chartData);
        
        return stats;
    }

    private LoanResponse convertToResponse(Loan loan) {
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
    }
    
    private TopCustomerResponse convertToTopCustomerResponse(Object[] data) {
        Long customerId = (Long) data[0];
        String customerName = (String) data[1];
        Long totalLoans = (Long) data[2];
        BigDecimal totalBorrowed = (BigDecimal) data[3];
        
        return new TopCustomerResponse(
            customerId,
            customerName,
            totalLoans.intValue(),
            totalBorrowed
        );
    }
    
    private List<ChartDataResponse> generateChartData(LocalDate startDate, List<Object[]> rawData) {
        Map<LocalDate, Integer> dataMap = new HashMap<>();
        for (Object[] row : rawData) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];
            dataMap.put(date, count.intValue());
        }
        List<ChartDataResponse> chartData = new ArrayList<>();
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            int value = dataMap.getOrDefault(currentDate, 0);
            String dayName = dayNames[currentDate.getDayOfWeek().getValue() % 7];
            chartData.add(new ChartDataResponse(dayName, value));
        }
        
        return chartData;
    }
}