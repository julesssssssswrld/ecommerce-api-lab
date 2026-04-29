package com.ws101.tomacas.EcommerceApi.service;

import com.ws101.tomacas.EcommerceApi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 *
 * <p>Loads user details from the database during the authentication
 * process. Spring Security's {@code DaoAuthenticationProvider} calls
 * this service automatically when a user attempts to log in.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see UserDetailsService
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the service with the injected user repository.
     *
     * @param userRepository the JPA repository for user lookups
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user by username for authentication.
     *
     * <p>Called by Spring Security during the login process.
     * The returned {@link UserDetails} object (our User entity)
     * provides the credentials and authorities for verification.</p>
     *
     * @param username the username to search for
     * @return the {@link UserDetails} matching the username
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
    }
}
