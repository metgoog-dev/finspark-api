package com.xyz.microfinance.repository;

import com.xyz.microfinance.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdCard(String idCard);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Boolean existsByIdCard(String idCard);
    Boolean existsByPhoneNumber(String phoneNumber);
    
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.phoneNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "c.idCard LIKE CONCAT('%', :searchTerm, '%')")
    List<Customer> searchCustomers(@Param("searchTerm") String searchTerm);
}