package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.entity.User;
import com.sunbase.clientmanager.exception.ClientManagerException;
import com.sunbase.clientmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves the authentication details of the currently authenticated user.
     * @throws ClientManagerException if authentication details are not found
     */
    @Override
    public Authentication getAuthenticationDetails(Authentication auth) {
        return Optional.ofNullable(auth)
                .orElseThrow(() -> new ClientManagerException("Authentication details not found"));
    }

    /**
     * Registers a new user and saves their details to the database.
     * @throws ClientManagerException if user registration fails
     */
    @Override
    public String registerUser(User user) {
        return Optional.ofNullable(user)
                .map(userRepository::save)
                .map(savedUser -> "Successfully registered: " + savedUser.getEmail())
                .orElseThrow(() -> new ClientManagerException("User registration failed"));
    }

}
