package com.sunbase.clientmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity class representing a Customer.
 * This class maps to the "Customer" table in the database.
 * It includes fields representing the customer's details.
 *
 * The class uses Lombok annotations to generate getters, setters,
 * a constructor with all arguments, and a no-argument constructor.
 *
 * The class is annotated with JPA annotations for ORM (Object-Relational Mapping).
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    /**
     * Unique identifier for the customer.
     */
    @Id
    private String uuid;

    /**
     * The first name of the customer.
     * This field is used to store the customer's first name.
     */
    private String firstName;

    /**
     * The last name of the customer.
     * This field is used to store the customer's last name.
     */
    private String lastName;

    /**
     * The street address of the customer.
     * This field is used to store the street part of the customer's address.
     */
    private String street;

    /**
     * The address line of the customer.
     * This field is used to store additional address information.
     */
    private String address;

    /**
     * The city where the customer resides.
     * This field is used to store the city of the customer's address.
     */
    private String city;

    /**
     * The state where the customer resides.
     * This field is used to store the state of the customer's address.
     */
    private String state;

    /**
     * The email address of the customer.
     * This field is used to store the customer's email address.
     * It should follow a valid email format.
     */
    private String email;

    /**
     * The phone number of the customer.
     * This field is used to store the customer's contact phone number.
     */
    private String phone;
}
