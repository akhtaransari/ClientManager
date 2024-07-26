package com.sunbase.clientmanager.exception;

/**
 * The ClientManagerException class represents a custom exception specific to the ClientManager application.
 * It extends RuntimeException to allow unchecked exceptions to be thrown and handled as needed.
 */
public class ClientManagerException extends RuntimeException {

    /**
     * Constructor to create a new instance of ClientManagerException with a specified message.
     *
     * @param message The detail message for the exception.
     *                This message provides information about the nature of the exception.
     */
    public ClientManagerException(String message) {
        super(message);
    }
}
