package com.xyz.microfinance.repository;

import com.xyz.microfinance.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerIdOrderByDateIssuedDesc(Long customerId);
    List<Loan> findByStatusOrderByDateIssuedDesc(String status);
    List<Loan> findByCustomerIdAndStatusNot(Long customerId, String status);
    
    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT SUM(l.principal) FROM Loan l WHERE l.status = 'ACTIVE'")
    Double getTotalActiveLoanAmount();
    
    @Query("SELECT l.customer.id, l.customer.name, COUNT(l), SUM(l.principal) " +
           "FROM Loan l " +
           "GROUP BY l.customer.id, l.customer.name " +
           "ORDER BY SUM(l.principal) DESC")
    List<Object[]> getTopCustomersByTotalBorrowed();
    @Query("SELECT CAST(l.dateIssued AS LocalDate) as day, COUNT(l) as count " +
           "FROM Loan l " +
           "WHERE l.dateIssued >= :startDate " +
           "GROUP BY CAST(l.dateIssued AS LocalDate) " +
           "ORDER BY CAST(l.dateIssued AS LocalDate)")
    List<Object[]> getLoanActivityForLast7Days(@Param("startDate") LocalDate startDate);
}