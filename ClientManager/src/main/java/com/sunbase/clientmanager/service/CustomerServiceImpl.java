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
     * @throws ClientManagerException if the customer is not found
     */
    @Override
    public Customer updateCustomer(String uuid, Customer customer) {

        if (!customerRepository.existsById(uuid)) {
            throw new ClientManagerException("Customer not found with ID: " + uuid);
        }
        customer.setUuid(uuid);
        return customerRepository.save(customer);
    }

    /**
     * Retrieves all customers with pagination, sorting, and searching.
     * @throws ClientManagerException if Invalid pagination or sorting parameters.
     */
    @Override
    public Page<Customer> getAllCustomers(int page, int size, String sortBy, String value) {

        if (page < 0 || size <= 0) {
            throw new ClientManagerException("Invalid pagination or sorting parameters.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

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
                return customerRepository.findAll(pageable);
        }
    }


    /**
     * Retrieves a customer by ID.
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
     * @throws ClientManagerException if the customer is not found or UUID is null
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
     * @throws ClientManagerException if no customer to update
     */
    @Override
    public String syncData(Password password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();

        String token = getToken(username, password.password());

        List<CustomerDTO> remoteCustomers = fetchCustomersFromRemoteApi(token);

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

        List<Customer> savedCustomers = customerRepository.saveAll(newCustomers);

        return savedCustomers.size() + " customers added successfully";
    }

    /**
     * Fetches customers from the remote API using the provided JWT for authorization.
     * @throws ClientManagerException if an error occurs while fetching customers
     */
    public List<CustomerDTO> fetchCustomersFromRemoteApi(String token) throws ClientManagerException {
        try {
            // Set up the headers with the JWT token and other required headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Execute the API call with headers
            ResponseEntity<List<CustomerDTO>> responseEntity = restTemplate.exchange(
                    REMOTE_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<CustomerDTO>>() {}
            );

            List<CustomerDTO> customers = responseEntity.getBody();
            if (customers == null || customers.isEmpty()) {
                throw new ClientManagerException("No customers found in the response from the remote API.");
            }
            return customers;
        } catch (HttpClientErrorException e) {
            throw new ClientManagerException("HTTP error occurred while fetching customers from the remote API: " + e.getMessage());
        } catch (Exception e) {
            throw new ClientManagerException("Failed to fetch customers from the remote API: " + e.getMessage());
        }
    }

    /**
     * Retrieves an authentication token for a user.
     * @throws ClientManagerException if an error occurs while retrieving the token
     */
    public String getToken(String email , String password) {
        // Create JSON payload with dynamic values
        String jsonPayload = String.format("{\"login_id\":\"%s\",\"password\":\"%s\"}", email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

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
                throw new ClientManagerException("Token not found in the response.");
            }
        } catch (HttpClientErrorException e) {
            throw new ClientManagerException("HTTP error occurred while retrieving the token: " + e.getMessage());
        } catch (Exception e) {
            throw new ClientManagerException("Failed to retrieve token: " + e.getMessage());
        }
    }
}
