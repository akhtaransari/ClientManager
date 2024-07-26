package com.sunbase.clientmanager.controller;

import com.sunbase.clientmanager.dto.UserDTO;
import com.sunbase.clientmanager.entity.Customer;
import com.sunbase.clientmanager.service.CustomerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sunbase.clientmanager.exception.ClientManagerException;
import lombok.extern.slf4j.Slf4j;


/**
 * Controller for managing customer operations.
 * Provides endpoints for creating, updating, retrieving, and deleting customers,
 * as well as syncing data and handling user login.
 */
@RestController
@RequestMapping("/api/customers")
@Slf4j
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerService;

    /**
     * Creates a new customer.
     * @param customer the customer data
     * @return a response entity with the created customer and HTTP status
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("Creating customer: {}", customer);
        Customer createdCustomer = customerService.createCustomer(customer);
        log.info("Customer created successfully: {}", createdCustomer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * Updates an existing customer.
     * @param id the ID of the customer to update
     * @param customer the new customer data
     * @return a response entity with the updated customer and HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        log.info("Updating customer with ID: {}", id);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        log.info("Customer updated successfully: {}", updatedCustomer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    /**
     * Retrieves a list of customers with pagination, sorting, and searching.
     * @param page the page number
     * @param size the page size
     * @param sortBy the sort criteria
     * @param value the search term
     * @return a response entity with the list of customers and HTTP status
     */
    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String value) {
        log.info("Retrieving customers - Page: {}, Size: {}, Sort: {}, Search: {}", page, size, sortBy, value);
        Page<Customer> customers = customerService.getAllCustomers(page, size, sortBy, value);
        log.info("Customers retrieved successfully");
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    /**
     * Retrieves a single customer by ID.
     * @param id the ID of the customer
     * @return a response entity with the customer and HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("Retrieving customer with ID: {}", id);
        Customer customer = customerService.getCustomerById(id);
        log.info("Customer retrieved successfully: {}", customer);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    /**
     * Deletes a customer by ID.
     * @param id the ID of the customer to delete
     * @return a response entity with HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
        log.info("Customer deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Synchronizes customer data from a remote API.
     * @return a response entity with the synchronization status and HTTP status
     */
    @GetMapping("/sync/{jwt}")
    public ResponseEntity<String> syncData(@PathVariable String jwt) {
        log.info("Synchronizing customer data");
        String syncStatus = customerService.syncData(jwt);
        log.info("Customer data synchronized successfully: {}", syncStatus);
        return new ResponseEntity<>(syncStatus, HttpStatus.OK);
    }

    /**
     * Handles user login requests.
     * This endpoint returns the authentication token of the currently authenticated user.
     * @param userDTO the authentication object containing user details
     * @return a response entity with the authentication token and HTTP status
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDTO userDTO) {
        log.info("User login attempt: {}", userDTO.username());
        Object token = customerService.getToken(userDTO);
        log.info("User logged in successfully: {}", userDTO.username());
        return new ResponseEntity<>(token, HttpStatus.ACCEPTED);
    }
}
