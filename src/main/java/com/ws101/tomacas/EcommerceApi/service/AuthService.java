package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.dto.RegisterUserDto;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for authentication-related operations.
 *
 * <p>Handles user registration, including password hashing
 * and username uniqueness validation.</p>
 *
 * @author Jules Ian C. Tomacas
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the auth service with required dependencies.
     *
     * @param userRepository  the JPA repository for user persistence
     * @param passwordEncoder the encoder for hashing passwords
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user account.
     *
     * <p>Validates that the username is not already taken, hashes
     * the plain-text password using BCrypt, and persists the user
     * to the database.</p>
     *
     * @param dto the registration data containing username, password, and role
     * @return the newly created User entity
     * @throws IllegalArgumentException if the username is already taken
     */
    public User register(RegisterUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException(
                    "Username '" + dto.getUsername() + "' is already taken.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        return userRepository.save(user);
    }
}
