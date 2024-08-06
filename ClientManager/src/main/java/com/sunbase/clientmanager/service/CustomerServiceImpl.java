package com.sunbase.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbase.clientmanager.dto.CustomerDTO;
import com.sunbase.clientmanager.dto.Password;
import com.sunbase.clientmanager.entity.Customer;
import com.sunbase.clientmanager.exception.ClientManagerException;
import com.sunbase.clientmanager.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service class for handling customer business logic.
 * Provides methods for CRUD operations, pagination, and synchronization.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    AuthService authService;

    private static final String REMOTE_API_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
    private static final String AUTH_URL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

    @Autowired
    private RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create
     * @return the created customer
     */
    @Override
    public Customer createCustomer(Customer customer) {
        // Generate UUID without hyphens
        String hexCode = UUID.randomUUID().toString().replace("-", "");

        // Prefix the UUID with "test"
        String customUuid = "test" + hexCode;

        // Set the custom UUID to the customer
        customer.setUuid(customUuid);

        // Save and return the customer
        return customerRepository.save(customer);
    }


    /**
     * Updates an existing customer.
     *
     * @param uuid the ID of the customer to update
     * @param customer the customer data to update
     * @return the updated customer
     * @throws ClientManagerException if the customer is not found
     */
    public Customer updateCustomer(String uuid, Customer customer) {

        // Check if customer exists by UUID
        if (!customerRepository.existsById(uuid)) {
            throw new ClientManagerException("Customer not found with ID: " + uuid);
        }
        // Ensure the provided customer object has the correct ID
        customer.setUuid(uuid);
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
     * @param uuid the ID of the customer
     * @return the customer
     * @throws ClientManagerException if the customer is not found
     */
    @Override
    public Customer getCustomerById(String uuid) {
        if (uuid == null) {
            throw new ClientManagerException("Customer ID cannot be null.");
        }
        return customerRepository.findById(uuid)
                .orElseThrow(() -> new ClientManagerException("Customer not found with ID: " + uuid));
    }


    /**
     * Deletes a customer by ID.
     *
     * @param uuid the ID of the customer to delete
     */
    @Override
    public void deleteCustomer(String uuid) {
        if (uuid == null) {
            throw new ClientManagerException("Customer ID cannot be null.");
        }
        if (!customerRepository.existsById(uuid)) {
            throw new ClientManagerException("Customer not found with ID: " + uuid);
        }
        customerRepository.deleteById(uuid);
    }


    /**
     * Syncs data by fetching customers from a remote API and saving unique customers to the database.
     * @return a success message
     */
    @Override
    public String syncData(Password password) {
        // Get the username from the authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();

        // Obtain the token using the username and password
        String token = getToken(username, password.password());

        // Fetch customers from the remote API
        List<CustomerDTO> remoteCustomers = fetchCustomersFromRemoteApi(token);

        // Fetch all customers from the local database
        List<Customer> localCustomers = customerRepository.findAll();
        Set<String> localCustomerUUIDs = localCustomers.stream()
                .map(Customer::getUuid)
                .collect(Collectors.toSet());

        // Filter out new customers that are not present in the local database
        List<Customer> newCustomers = new ArrayList<>();
        for (CustomerDTO rc : remoteCustomers) {
            if (!localCustomerUUIDs.contains(rc.getUuid())) {
                Customer customer = new Customer(
                        rc.getUuid(), rc.getFirstName(), rc.getLastName(),
                        rc.getStreet(), rc.getAddress(), rc.getCity(),
                        rc.getState(), rc.getEmail(), rc.getPhone());
                newCustomers.add(customer);
            }
        }

        if (newCustomers.isEmpty()) {
            throw new ClientManagerException("No Customers to update");
        }

        // Save all new customers to the local database
        List<Customer> savedCustomers = customerRepository.saveAll(newCustomers);

        return savedCustomers.size() + " customers added successfully";
    }




    /**
     * Fetches customers from the remote API using the provided JWT for authorization.
     *
     * @param token the JSON Web Token for authorization
     * @return a list of customers from the remote API
     * @throws ClientManagerException if an error occurs while fetching customers
     */
    public List<CustomerDTO> fetchCustomersFromRemoteApi(String token) throws ClientManagerException {
        try {
            // Set up the headers with the JWT token and other required headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            // Create an HttpEntity object with the headers
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Execute the API call with headers
            ResponseEntity<List<CustomerDTO>> responseEntity = restTemplate.exchange(
                    REMOTE_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<CustomerDTO>>() {}
            );

            // Get the list of customers from the response body
            List<CustomerDTO> customers = responseEntity.getBody();
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
     * @param email,password the user credentials
     * @return the authentication token
     * @throws ClientManagerException if an error occurs while retrieving the token
     */
    public String getToken(String email , String password) {
        // Create JSON payload with dynamic values
        String jsonPayload = String.format("{\"login_id\":\"%s\",\"password\":\"%s\"}", email, password);

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

            // Parse the response to extract the token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());

            // Extract the token from the JSON response
            JsonNode tokenNode = jsonNode.get("access_token");
            if (tokenNode != null) {
                return tokenNode.asText();
            } else {
                // Handle the case where the token is not present in the response
                throw new ClientManagerException("Token not found in the response.");
            }
        } catch (HttpClientErrorException e) {
            // Log the specific HTTP error details
            throw new ClientManagerException("HTTP error occurred while retrieving the token: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors
            throw new ClientManagerException("Failed to retrieve token: " + e.getMessage());
        }
    }
}
