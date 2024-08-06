package com.sunbase.clientmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a User.
 * This class maps to a database table and is used to persist user data.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        @Size(max = 50, message = "Email must be less than 50 characters")
        @Column(nullable = false, unique = true, length = 50)
        private String email;

        @NotBlank(message = "Password is mandatory")
        @Size(max = 100, message = "Password must be less than 100 characters")
        @Column(nullable = false, length = 100)
        private String password;

        @NotBlank(message = "Role is mandatory")
        @Size(max = 20, message = "Role must be less than 20 characters")
        @Column(nullable = false, length = 20)
        private String role;
}
