package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.dto.UserDTO;
import com.sunbase.clientmanager.entity.Customer;
import com.sunbase.clientmanager.exception.ClientManagerException;
import com.sunbase.clientmanager.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for handling customer business logic.
 * Provides methods for CRUD operations, pagination, and synchronization.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String REMOTE_API_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
    private static final String AUTH_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

    @Autowired
    private RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create
     * @return the created customer
     */
    @Override
    public Customer createCustomer(Customer customer) {
        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new ClientManagerException("Customer or customer email cannot be null or empty.");
        }
        return customerRepository.save(customer);
    }

    /**
     * Updates an existing customer.
     *
     * @param id the ID of the customer to update
     * @param customer the customer data to update
     * @return the updated customer
     * @throws ClientManagerException if the customer is not found
     */
    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        if (id == null || customer == null) {
            throw new ClientManagerException("Customer ID or customer cannot be null.");
        }
        if (!customerRepository.existsById(id)) {
            throw new ClientManagerException("Customer not found with ID: " + id);
        }
        customer.setId(id);
        return customerRepository.save(customer);
    }

    /**
     * Retrieves all customers with pagination, sorting, and searching.
     *
     * @param page   the page number
     * @param size   the page size
     * @param sortBy the sort criteria
     * @param value  the search term
     * @return the list of customers
     */
    @Override
    public Page<Customer> getAllCustomers(int page, int size, String sortBy, String value) {
        // Validate pagination and sorting parameters
        if (page < 0 || size <= 0) {
            throw new ClientManagerException("Invalid pagination or sorting parameters.");
        }

        // Create Pageable instance with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // Fetch data based on the sortBy parameter
        switch (sortBy.toLowerCase()) {
            case "firstname":
                return customerRepository.findByFirstName(value, pageable);
            case "city":
                return customerRepository.findByCity(value, pageable);
            case "email":
                return customerRepository.findByEmail(value, pageable);
            case "phone":
                return customerRepository.findByPhone(value, pageable);
            default:
                // Handle cases where sortBy does not match any predefined fields
                return customerRepository.findAll(pageable);
        }
    }


    /**
     * Retrieves a customer by ID.
     *
     * @param id the ID of the customer
     * @return the customer
     * @throws ClientManagerException if the customer is not found
     */
    @Override
    public Customer getCustomerById(Long id) {
        if (id == null) {
            throw new ClientManagerException("Customer ID cannot be null.");
        }
        return customerRepository.findById(id)
                .orElseThrow(() -> new ClientManagerException("Customer not found with ID: " + id));
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id the ID of the customer to delete
     */
    @Override
    public void deleteCustomer(Long id) {
        if (id == null) {
            throw new ClientManagerException("Customer ID cannot be null.");
        }
        if (!customerRepository.existsById(id)) {
            throw new ClientManagerException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    /**
     * Syncs data by fetching customers from a remote API and saving unique customers to the database.
     *
     * @param jwt the JSON Web Token for authorization
     * @return a success message
     */
    @Override
    public String syncData(String jwt) {
        List<Customer> remoteCustomers = fetchCustomersFromRemoteApi(jwt);
        List<Customer> localCustomers = customerRepository.findAll();

        // Find unique customers by email ID
        Set<String> localEmails = localCustomers.stream()
                .map(Customer::getEmail)
                .collect(Collectors.toSet());

        List<Customer> newCustomers = remoteCustomers.stream()
                .filter(customer -> customer.getEmail() != null && !localEmails.contains(customer.getEmail()))
                .collect(Collectors.toList());

        if (newCustomers.isEmpty()) {
            throw new ClientManagerException("No new customers to sync.");
        }

        customerRepository.saveAll(newCustomers);
        return "Sync Successful";
    }

    /**
     * Fetches customers from the remote API using the provided JWT for authorization.
     *
     * @param jwt the JSON Web Token for authorization
     * @return a list of customers from the remote API
     * @throws ClientManagerException if an error occurs while fetching customers
     */
    public List<Customer> fetchCustomersFromRemoteApi(String jwt) throws ClientManagerException {
        try {
            // Set up the headers with the JWT token and other required headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            // Create an HttpEntity object with the headers
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Execute the API call with headers
            ResponseEntity<List<Customer>> responseEntity = restTemplate.exchange(
                    REMOTE_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Customer>>() {}
            );

            List<Customer> customers = responseEntity.getBody();
            if (customers == null || customers.isEmpty()) {
                throw new ClientManagerException("No customers found in the response from the remote API.");
            }
            return customers;
        } catch (HttpClientErrorException e) {
            // Log the specific HTTP error details
            throw new ClientManagerException("HTTP error occurred while fetching customers from the remote API: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors
            throw new ClientManagerException("Failed to fetch customers from the remote API: " + e.getMessage());
        }
    }

    /**
     * Retrieves an authentication token for a user.
     *
     * @param userDTO the user credentials
     * @return the authentication token
     * @throws ClientManagerException if an error occurs while retrieving the token
     */
    @Override
    public Object getToken(UserDTO userDTO) {
        // Create JSON payload with dynamic values
        String jsonPayload = String.format("{\"login_id\":\"%s\",\"password\":\"%s\"}", userDTO.username(), userDTO.password());

        // Create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up the headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create the HttpEntity with the JSON payload and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

        try {
            // Make the POST request and get the response
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    AUTH_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return responseEntity.getBody();

        } catch (Exception e) {
            throw new ClientManagerException("Token error: " + e.getMessage());
        }
    }
}
