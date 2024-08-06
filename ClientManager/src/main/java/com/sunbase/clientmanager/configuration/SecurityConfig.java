package com.sunbase.clientmanager.configuration;

import java.util.Arrays;
import java.util.Collections;

import com.sunbase.clientmanager.security.JwtTokenGeneratorFilter;
import com.sunbase.clientmanager.security.JwtTokenValidatorFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Security configuration for the application.
 * Configures HTTP security, CORS settings, CSRF protection, and filter chains.
 */
@Slf4j
@Configuration
public class SecurityConfig {

	/**
	 * Configures security filter chain for the application.
	 *
	 * @param http the HttpSecurity object
	 * @return the SecurityFilterChain object
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// Configure CSRF token request attribute handler
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

		http
				// Configure session management to be stateless
				.sessionManagement(sessionManagement ->
						sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				// Configure CORS settings
				.cors(cors -> {
					cors.configurationSource(new CorsConfigurationSource() {
						@Override
						public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
							CorsConfiguration cfg = new CorsConfiguration();
							cfg.setAllowedOriginPatterns(Collections.singletonList("*"));
							cfg.setAllowedMethods(Collections.singletonList("*"));
							cfg.setAllowCredentials(true);
							cfg.setAllowedHeaders(Collections.singletonList("*"));
							cfg.setExposedHeaders(Arrays.asList("Authorization"));
							// Log CORS configuration
							log.info("CORS configuration applied: {}", cfg);
							return cfg;
						}
					});
				})
				// Configure HTTP request authorization
				.authorizeHttpRequests(auth -> {
					auth
							.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
							.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
							.requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
							.anyRequest().authenticated();
					// Log authorization rules
					log.info("Authorization rules configured.");
				})
				// Disable CSRF protection
				.csrf(csrf -> {
					csrf.disable();
					// Log CSRF configuration
					log.info("CSRF protection disabled.");
				})
				// Add custom JWT token validator filter before BasicAuthenticationFilter
				.addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
				// Add custom JWT token generator filter after BasicAuthenticationFilter
				.addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
				// Configure form login
				.formLogin(Customizer.withDefaults())
				// Configure basic authentication
				.httpBasic(Customizer.withDefaults());

		// Log security filter chain configuration
		log.info("Security filter chain configured.");

		return http.build();
	}

	/**
	 * Configures the password encoder to use BCrypt.
	 *
	 * @return the PasswordEncoder object
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// Log password encoder creation
		log.info("Password encoder (BCrypt) created.");
		return new BCryptPasswordEncoder();
	}
}
