package com.sunbase.clientmanager.repository;

import com.sunbase.clientmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations and custom query capabilities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a {@link User} entity by email.
     * This method searches for a user whose email matches the provided value.
     * If a matching user is found, it is returned wrapped in an {@link Optional}.
     * If no match is found, an empty {@link Optional} is returned.
     *
     * @param email the email to search for, cannot be {@code null}
     * @return an {@link Optional} containing the matching user if found, or an empty {@link Optional} if no user matches
     */
    Optional<User> findByEmail(String email);
}
