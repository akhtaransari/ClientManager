package com.sunbase.clientmanager.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO class representing a UserDTO.
 * This class is used to transfer user data.
 */
public record UserDTO(

        @NotBlank(message = "Username is mandatory")
        String username,

        @NotBlank(message = "Password is mandatory")
        String password
) {}
