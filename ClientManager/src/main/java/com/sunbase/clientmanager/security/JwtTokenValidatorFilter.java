package com.sunbase.clientmanager.security;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for validating JWT tokens.
 * This filter extends OncePerRequestFilter to ensure that the JWT token is validated
 * once per request, and authentication is set in the security context.
 */
@Slf4j
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

	/**
	 * Validates the JWT token from the request header and sets the authentication
	 * information in the security context if the token is valid.
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
		// Retrieve the JWT token from the request header
		String jwtToken = request.getHeader(SecurityConstants.JWT_HEADER);

		if (jwtToken != null) {
			try {
				// Remove the "Bearer " prefix from the token if present
				jwtToken = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

				// Generate the secret key for validating the JWT token
				SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

				// Parse the JWT token and extract claims
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(secretKey)
						.build()
						.parseClaimsJws(jwtToken)
						.getBody();

				// Extract username and authorities from the claims
				String username = claims.get("username", String.class);
				String authorities = claims.get("authorities", String.class);

				// Convert authorities from comma-separated string to list of GrantedAuthority
				List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

				// Create an Authentication object with the extracted username and authorities
				Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);

				// Set the authentication object in the security context
				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.info("JWT Token validated and authentication set for user: {}", username);

			} catch (Exception e) {
				log.error("Error validating JWT Token: {}", e.getMessage());
				throw new BadCredentialsException("Invalid Token received.");
			}
		}

		// Continue with the next filter in the chain
		filterChain.doFilter(request, response);
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
		// Apply the filter to all paths except the /api/auth/login endpoint
		boolean shouldNotFilter = request.getServletPath().equals("/api/auth/login");
		log.info("Filter should {} be applied to path: {}", shouldNotFilter ? "not" : "be", request.getServletPath());
		return shouldNotFilter;
	}
}
