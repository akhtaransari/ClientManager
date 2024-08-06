package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.entity.User;
import org.springframework.security.core.Authentication;

/**
 * Service interface for handling authentication-related operations.
 */
public interface AuthService {

    /**
     * Retrieves the authentication details of the currently authenticated user.
     *
     * @param auth the authentication object containing user details
     * @return the authentication details of the currently authenticated user
     */
    Authentication getAuthenticationDetails(Authentication auth);

    /**
     * Registers a new user and saves their details to the database.
     *
     * @param user the User object containing the details of the user to register
     * @return the String of successfully registration of user
     */
    String registerUser(User user);
}
