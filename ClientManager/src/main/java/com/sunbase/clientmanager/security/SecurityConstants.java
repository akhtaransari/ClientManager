package com.sunbase.clientmanager.security;

/**
 * SecurityConstants class holds constant values used for security configuration in the application.
 * This includes JWT key and header constants.
 */
public interface SecurityConstants {

	/**
	 * The secret key used for signing and verifying JWT tokens.
	 * This key should be kept confidential and secure.
	 * It is used to ensure the integrity and authenticity of the JWT token.
	 */
	public static final String JWT_KEY = "your-secure-generated-key-with-jwt-details";

	/**
	 * The name of the HTTP header used to pass the JWT token in requests.
	 * This header should be included in requests to protected endpoints.
	 */
	public static final String JWT_HEADER = "Authorization";
}
