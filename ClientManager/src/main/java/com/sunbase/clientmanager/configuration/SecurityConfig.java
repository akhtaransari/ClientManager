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
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

		http
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
							return cfg;
						}
					});
				})
				// Configure HTTP request authorization
				.authorizeHttpRequests(auth -> {
					auth
							.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
							.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
							.anyRequest().authenticated();
					log.info("Authorization rules configured.");
				})

				.csrf(csrf -> {
					csrf.disable();
					log.info("CSRF protection disabled.");
				})
				.addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults());

		log.info("Security filter chain configured.");

		return http.build();
	}

	/**
	 * Configures the password encoder to use BCrypt.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		log.info("Password encoder (BCrypt) created.");
		return new BCryptPasswordEncoder();
	}
}
