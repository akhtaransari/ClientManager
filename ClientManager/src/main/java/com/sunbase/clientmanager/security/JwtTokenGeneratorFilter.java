package com.sunbase.clientmanager.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter for generating and adding a JWT token to the response header.
 * This filter extends OncePerRequestFilter to ensure that the JWT token is generated
 * and added to the response header only once per request.
 */
@Slf4j
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

	/**
	 * Generates a JWT token if the authentication information is available.
	 * Adds the generated token to the response header.
	 *
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param filterChain the filter chain
	 * @throws ServletException if an error occurs during filtering
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("Processing request to generate JWT Token...");

		// Retrieve authentication information from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			log.info("Authentication details found. Generating JWT Token.");

			// Extract authorities from the authentication object
			log.info("User authorities: {}", authentication.getAuthorities());

			// Generate a secret key for signing the JWT
			SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

			// Build the JWT token
			String jwtToken = Jwts.builder()
					.setIssuer("ADMIN")  // Issuer of the token
					.setSubject("JWT Token")  // Subject of the token
					.claim("username", authentication.getName())  // Add username as a claim
					.claim("authorities", populateAuthorities(authentication.getAuthorities()))  // Add user authorities as a claim
					.setIssuedAt(new Date())  // Set the token issue date
					.setExpiration(new Date(new Date().getTime() + 30000000))  // Set the token expiration date
					.signWith(secretKey)  // Sign the token with the secret key
					.compact();

			// Add the JWT token to the response header
			response.setHeader(SecurityConstants.JWT_HEADER, jwtToken);
			log.info("JWT Token generated and added to the response header.");
		} else {
			log.info("No authentication information found. Skipping JWT Token generation.");
		}

		// Continue with the next filter in the chain
		filterChain.doFilter(request, response);
	}

	/**
	 * Converts a collection of GrantedAuthority objects to a comma-separated string.
	 *
	 * @param collection the collection of GrantedAuthority objects
	 * @return a comma-separated string of authority names
	 */
	private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
		Set<String> authorities = new HashSet<>();
		for (GrantedAuthority auth : collection) {
			authorities.add(auth.getAuthority());
		}
		return String.join(",", authorities);
	}

	/**
	 * Determines whether this filter should be applied to the current request.
	 *
	 * @param request the HTTP request
	 * @return true if the filter should not be applied, false otherwise
	 * @throws ServletException if an error occurs during the decision
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// Apply the filter only for the /api/auth/login endpoint
		boolean shouldNotFilter = !request.getServletPath().equals("/api/auth/login");
		log.info("Filter should {} be applied to path: {}", shouldNotFilter ? "not" : "be", request.getServletPath());
		return shouldNotFilter;
	}
}
