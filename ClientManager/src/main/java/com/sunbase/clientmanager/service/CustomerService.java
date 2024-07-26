package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.dto.UserDTO;
import com.sunbase.clientmanager.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {

    /**
     * Creates a new customer.
     * @param customer the customer to create
     * @return the created customer
     */
    Customer createCustomer(Customer customer);

    /**
     * Updates an existing customer.
     * @param id the ID of the customer to update
     * @param customer the customer data to update
     * @return the updated customer
     */
    Customer updateCustomer(Long id, Customer customer);


    Page<Customer> getAllCustomers(int page, int size, String sort, String search);

    /**
     * Retrieves a customer by ID.
     * @param id the ID of the customer
     * @return the customer
     */
    Customer getCustomerById(Long id);

    /**
     * Deletes a customer by ID.
     * @param id the ID of the customer to delete
     */
    void deleteCustomer(Long id);

    /**
     * Syncs data by fetching customers from a remote API and saving unique customers to the database.
     * @return a success message
     */
    String syncData(String jwt);

    /**
     * Retrieves an authentication token for a user.
     * @param userDTO the user credentials
     * @return the authentication token
     */
    Object getToken(UserDTO userDTO);
}
