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

@RestController
@RequestMapping("/api/customers")
@Slf4j
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerService;

    /**
     * Creates a new customer.
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
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncData(@RequestBody Password password) {
        log.info("Synchronizing customer data");
        String syncStatus = customerService.syncData(password);

        log.info("Customer data synchronized successfully: {}", syncStatus);
        return new ResponseEntity<>(syncStatus, HttpStatus.OK);
    }
}
