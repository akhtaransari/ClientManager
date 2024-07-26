package com.sunbase.clientmanager.repository;

import com.sunbase.clientmanager.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for Customer entity.
 * Extends JpaRepository to provide CRUD operations and additional JPA functionalities.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds customers by their first name with pagination support.
     *
     * @param value the first name to search for
     * @param pageable the pagination information
     * @return a page of customers with the given first name
     */
    @Query("SELECT c FROM Customer c WHERE c.firstName = :value")
    Page<Customer> findByFirstName(String value, Pageable pageable);

    /**
     * Finds customers by their city with pagination support.
     *
     * @param value the city to search for
     * @param pageable the pagination information
     * @return a page of customers with the given city
     */
    @Query("SELECT c FROM Customer c WHERE c.city = :value")
    Page<Customer> findByCity(String value, Pageable pageable);

    /**
     * Finds customers by their email with pagination support.
     *
     * @param value the email to search for
     * @param pageable the pagination information
     * @return a page of customers with the given email
     */
    @Query("SELECT c FROM Customer c WHERE c.email = :value")
    Page<Customer> findByEmail(String value, Pageable pageable);

    /**
     * Finds customers by their phone number with pagination support.
     *
     * @param value the phone number to search for
     * @param pageable the pagination information
     * @return a page of customers with the given phone number
     */
    @Query("SELECT c FROM Customer c WHERE c.phone = :value")
    Page<Customer> findByPhone(String value, Pageable pageable);
}
