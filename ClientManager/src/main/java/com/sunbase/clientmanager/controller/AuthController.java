package com.sunbase.clientmanager.controller;

import com.sunbase.clientmanager.entity.User;
import com.sunbase.clientmanager.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related requests.
 * This controller manages user login and registration operations.
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Handles user login requests.
     * This endpoint returns the authentication details of the currently authenticated user.
     *
     * @param auth the authentication object containing user details
     * @return a ResponseEntity containing the authentication details and HTTP status ACCEPTED
     */
    @GetMapping("/login")
    public ResponseEntity<Authentication> login(Authentication auth) {
        log.info("User logged in: {}", auth.getName());
        // Return authentication details of the currently logged-in user
        return new ResponseEntity<>(authService.getAuthenticationDetails(auth), HttpStatus.ACCEPTED);
    }

    /**
     * Handles user registration requests.
     * This endpoint registers a new user and saves their details to the database.
     *
     * @param user the User object containing the details of the user to register
     * @return a ResponseEntity containing the registered user and HTTP status CREATED
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        log.info("Registering new user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String result = authService.registerUser(user);
        // Log the registration success
        log.info("User registered successfully with email: {}", user.getEmail());
        // Return the registered user with HTTP status CREATED
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
