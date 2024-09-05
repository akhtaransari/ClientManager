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


@Slf4j
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

	/**
	 * Generates a JWT token if the authentication information is available.
	 * @throws ServletException if an error occurs during filtering
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("Processing request to generate JWT Token...");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			log.info("Authentication details found. Generating JWT Token.");

			SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

			// Build the JWT token
			String jwtToken = Jwts.builder()
					.setIssuer("ADMIN")
					.setSubject("JWT Token")
					.claim("username", authentication.getName())
					.claim("authorities", populateAuthorities(authentication.getAuthorities()))
					.setIssuedAt(new Date())
					.setExpiration(new Date(new Date().getTime() + 30000000))
					.signWith(secretKey)
					.compact();

			response.setHeader(SecurityConstants.JWT_HEADER, jwtToken);
			log.info("JWT Token generated and added to the response header.");
		} else {
			log.info("No authentication information found. Skipping JWT Token generation.");
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Converts a collection of GrantedAuthority objects to a comma-separated string.
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
	 * @throws ServletException if an error occurs during the decision
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		boolean shouldNotFilter = !request.getServletPath().equals("/api/auth/login");
		log.info("Filter should {} be applied to path: {}", shouldNotFilter ? "not" : "be", request.getServletPath());
		return shouldNotFilter;
	}
}
