package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.dto.Password;
import com.sunbase.clientmanager.entity.Customer;
import org.springframework.data.domain.Page;

/**
 * Service interface for handling customer-related operations.
 */
public interface CustomerService {

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create
     * @return the created customer
     */
    Customer createCustomer(Customer customer);

    /**
     * Updates an existing customer.
     *
     * @param uuid the ID of the customer to update
     * @param customer the customer data to update
     * @return the updated customer
     */
    Customer updateCustomer(String uuid, Customer customer);

    /**
     * Retrieves all customers with pagination, sorting, and searching.
     *
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @param sort the field to sort the results by
     * @param search the search term to filter the results
     * @return a page of customers matching the criteria
     */
    Page<Customer> getAllCustomers(int page, int size, String sort, String search);

    /**
     * Retrieves a customer by ID.
     *
     * @param uuid the ID of the customer
     * @return the customer with the specified ID
     */
    Customer getCustomerById(String uuid);

    /**
     * Deletes a customer by ID.
     *
     * @param uuid the UUID of the customer to delete
     */
    void deleteCustomer(String uuid);

    /**
     * Syncs data by fetching customers from a remote API and saving unique customers to the database.
     *
     * @param password the password for authentication
     * @return a success message indicating the number of customers added
     */
    String syncData(Password password);
}
