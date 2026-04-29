package com.ws101.tomacas.EcommerceApi.repository;

import com.ws101.tomacas.EcommerceApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for {@link User} entities.
 *
 * <p>Provides standard CRUD operations plus custom query methods
 * for looking up users by username during authentication.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with that username exists
     */
    boolean existsByUsername(String username);
}
