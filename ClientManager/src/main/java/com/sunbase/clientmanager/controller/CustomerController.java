package com.sunbase.clientmanager.controller;

import com.sunbase.clientmanager.dto.Password;
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
     * @param uuid the ID of the customer to update
     * @param customer the new customer data
     * @return a response entity with the updated customer and HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String uuid, @RequestBody Customer customer) {
        log.info("Updating customer with ID: {}", uuid);
        Customer updatedCustomer = customerService.updateCustomer(uuid, customer);
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
            @RequestParam(defaultValue = "uuid") String sortBy,
            @RequestParam(defaultValue = "") String value) {
        log.info("Retrieving customers - Page: {}, Size: {}, Sort: {}, Search: {}", page, size, sortBy, value);
        Page<Customer> customers = customerService.getAllCustomers(page, size, sortBy, value);
        log.info("Customers retrieved successfully");
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    /**
     * Retrieves a single customer by ID.
     * @param uuid the ID of the customer
     * @return a response entity with the customer and HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String uuid) {
        log.info("Retrieving customer with ID: {}", uuid);
        Customer customer = customerService.getCustomerById(uuid);
        log.info("Customer retrieved successfully: {}", customer);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    /**
     * Deletes a customer by ID.
     * @param uuid the ID of the customer to delete
     * @return a response entity with HTTP status
     * @throws ClientManagerException if the customer is not found
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String uuid) {
        log.info("Deleting customer with ID: {}", uuid);
        customerService.deleteCustomer(uuid);
        log.info("Customer deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Synchronizes customer data from a remote API.
     * @param password the password object containing the user's password
     * @return a response entity with the synchronization status and HTTP status
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncData(@RequestBody Password password) {
        log.info("Synchronizing customer data");
        String syncStatus = customerService.syncData(password);
        log.info("Customer data synchronized successfully: {}", syncStatus);
        return new ResponseEntity<>(syncStatus, HttpStatus.OK);
    }
}
