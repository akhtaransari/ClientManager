package com.sunbase.clientmanager.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * The GlobalExceptionHandler class handles exceptions globally across the application.
 * It provides a centralized way to manage different types of exceptions and format them into a consistent response structure.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles NoHandlerFoundException which occurs when no handler is found for a given request.
     *
     * @param ex The exception thrown when no handler is found.
     * @param wr The web request that caused the exception.
     * @return A ResponseEntity containing the error details and an HTTP status of BAD_REQUEST.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetails> noHandler(NoHandlerFoundException ex, WebRequest wr) {
        log.warn("NoHandlerFoundException: " + ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), wr.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions that do not fall into specific categories.
     *
     * @param ex The exception thrown.
     * @param wr The web request that caused the exception.
     * @return A ResponseEntity containing the error details and an HTTP status of BAD_REQUEST.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> parentException(Exception ex, WebRequest wr) {
        log.warn("Exception: " + ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), wr.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException which occurs when method arguments are not valid.
     *
     * @param ex The exception thrown for invalid method arguments.
     * @param wr The web request that caused the exception.
     * @return A ResponseEntity containing the error details and an HTTP status of BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgValidException(MethodArgumentNotValidException ex, WebRequest wr) {
        log.warn("MethodArgumentNotValidException: " + ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), wr.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom exceptions specific to the ClientManager application.
     *
     * @param ex The custom exception thrown by the ClientManager application.
     * @param wr The web request that caused the exception.
     * @return A ResponseEntity containing the error details and an HTTP status of BAD_REQUEST.
     */
    @ExceptionHandler(ClientManagerException.class)
    public ResponseEntity<ErrorDetails> gymException(ClientManagerException ex, WebRequest wr) {
        log.warn("ClientManagerException: " + ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), wr.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
