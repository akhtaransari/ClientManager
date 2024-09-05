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

@Service
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Loads user-specific data by email.
	 * @throws UsernameNotFoundException if the user is not found in the database
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Attempting to load user by email: {}", email);

		Optional<User> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			log.info("User found: {}", user.getEmail());

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

			// Return a Spring Security UserDetails object with the user's email, password, and authorities
			return new org.springframework.security.core.userdetails.User(
					user.getEmail(),
					user.getPassword(),
					grantedAuthorities
			);
		} else {
			log.warn("User not found with email: {}", email);
			throw new BadCredentialsException("User Details not found with this email: " + email);
		}
	}
}
