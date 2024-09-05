package com.sunbase.clientmanager.repository;

import com.sunbase.clientmanager.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c FROM Customer c WHERE c.firstName = :value")
    Page<Customer> findByFirstName(String value, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.city = :value")
    Page<Customer> findByCity(String value, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.email = :value")
    Page<Customer> findByEmail(String value, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.phone = :value")
    Page<Customer> findByPhone(String value, Pageable pageable);
}
