package com.sunbase.clientmanager.exception;

import java.time.LocalDateTime;

/**
 * The ErrorDetails class represents the structure of error details in the API response.
 * It encapsulates the information about an error that occurred during API operations.
 * This class is designed to provide meaningful error messages and details to the client.
 */
public record ErrorDetails(
        /*
         * A brief message describing the error that occurred.
         * For example, "Resource not found" or "Invalid input".
         */
        String message,

        /*
         * A detailed description of the error.
         * This could include additional context or information to help understand the cause of the error.
         */
        String description,

        /*
         * The timestamp when the error occurred.
         * This helps in tracking when the error happened and can be useful for debugging.
         */
        LocalDateTime timeStamp) {
}
