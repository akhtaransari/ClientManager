package com.sunbase.clientmanager.service;

import com.sunbase.clientmanager.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthService {

    Authentication getAuthenticationDetails(Authentication auth);

    String registerUser(User user);
}
