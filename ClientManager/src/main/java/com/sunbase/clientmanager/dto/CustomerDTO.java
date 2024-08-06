package com.sunbase.clientmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for representing customer data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    /**
     * Unique identifier for the customer.
     */
    private String uuid;

    /**
     * First name of the customer.
     */
    private String firstName;

    /**
     * Last name of the customer.
     */
    private String lastName;

    /**
     * Street address of the customer.
     */
    private String street;

    /**
     * Additional address information for the customer.
     */
    private String address;

    /**
     * City where the customer resides.
     */
    private String city;

    /**
     * State where the customer resides.
     */
    private String state;

    /**
     * Email address of the customer.
     */
    private String email;

    /**
     * Phone number of the customer.
     */
    private String phone;
}
