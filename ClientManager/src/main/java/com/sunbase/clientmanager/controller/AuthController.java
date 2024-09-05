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
     */
    @GetMapping("/login")
    public ResponseEntity<Authentication> login(Authentication auth) {
        log.info("User logged in: {}", auth.getName());

        return new ResponseEntity<>(authService.getAuthenticationDetails(auth), HttpStatus.ACCEPTED);
    }

    /**
     * Handles user registration requests.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        log.info("Registering new user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String result = authService.registerUser(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
