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

@Slf4j
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

	/**
	 * Validates the JWT token from the request header and sets the authentication
	 * @throws ServletException if an error occurs during filtering
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwtToken = request.getHeader(SecurityConstants.JWT_HEADER);

		if (jwtToken != null) {
			try {
				jwtToken = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;

				SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

				// Parse the JWT token and extract claims
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(secretKey)
						.build()
						.parseClaimsJws(jwtToken)
						.getBody();


				String username = claims.get("username", String.class);
				String authorities = claims.get("authorities", String.class);

				List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

				Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);

				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.info("JWT Token validated and authentication set for user: {}", username);

			} catch (Exception e) {
				log.error("Error validating JWT Token: {}", e.getMessage());
				throw new BadCredentialsException("Invalid Token received.");
			}
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Determines whether this filter should be applied to the current request.
	 * @throws ServletException if an error occurs during the decision
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		boolean shouldNotFilter = request.getServletPath().equals("/api/auth/login");
		log.info("Filter should {} be applied to path: {}", shouldNotFilter ? "not" : "be", request.getServletPath());
		return shouldNotFilter;
	}
}
