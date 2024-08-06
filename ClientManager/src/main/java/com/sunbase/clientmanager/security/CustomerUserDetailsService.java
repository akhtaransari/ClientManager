package com.sunbase.clientmanager.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sunbase.clientmanager.entity.User;
import com.sunbase.clientmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for loading user-specific data for Spring Security.
 * This service fetches user details from the database and converts them into
 * Spring Security's UserDetails object for authentication and authorization.
 */
@Service
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Loads user-specific data by email.
	 *
	 * This method is called by Spring Security during authentication to retrieve
	 * the user details from the database using the provided email.
	 * It constructs a UserDetails object that includes user authorities.
	 *
	 * @param email the email of the user whose details are to be loaded
	 * @return a UserDetails object containing user information and granted authorities
	 * @throws UsernameNotFoundException if the user is not found in the database
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Attempting to load user by email: {}", email);

		// Retrieve the user from the database based on the provided email
		Optional<User> optionalUser = userRepository.findByEmail(email);

		// Check if the user exists
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			log.info("User found: {}", user.getEmail());

			// Convert user roles to Spring Security authorities
			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

			// Return a Spring Security UserDetails object with the user's email, password, and authorities
			return new org.springframework.security.core.userdetails.User(
					user.getEmail(),
					user.getPassword(),
					grantedAuthorities
			);
		} else {
			// Log a warning and throw an exception if the user is not found
			log.warn("User not found with email: {}", email);
			throw new BadCredentialsException("User Details not found with this email: " + email);
		}
	}
}
