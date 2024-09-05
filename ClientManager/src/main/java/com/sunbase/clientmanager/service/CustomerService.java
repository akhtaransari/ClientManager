package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.dto.Password;
import com.sunbase.clientmanager.entity.Customer;
import org.springframework.data.domain.Page;


public interface CustomerService {


    Customer createCustomer(Customer customer);

    Customer updateCustomer(String uuid, Customer customer);

    Page<Customer> getAllCustomers(int page, int size, String sort, String search);

    Customer getCustomerById(String uuid);

    void deleteCustomer(String uuid);

    String syncData(Password password);
}
