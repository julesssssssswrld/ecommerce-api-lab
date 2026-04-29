package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.dto.RegisterUserDto;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 *
 * <p>Provides endpoints for user registration and retrieving
 * the currently authenticated user's information. Login and
 * logout are handled by Spring Security's form login mechanism.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see AuthService
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs the controller with the auth service.
     *
     * @param authService the service handling registration logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * <p>This endpoint is publicly accessible so that new users
     * can sign up without being authenticated first. The password
     * is hashed before storage.</p>
     *
     * @param dto the registration data (username, password, role)
     * @return a 201 Created response with the new user's info
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterUserDto dto) {
        User user = authService.register(dto);

        // Return user info without the password hash
        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "message", "User registered successfully"
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Returns the currently authenticated user's information.
     *
     * <p>Uses {@code @AuthenticationPrincipal} to inject the
     * logged-in user directly from the security context. Returns
     * 401 if no user is authenticated.</p>
     *
     * @param user the currently logged-in user
     * @return the user's ID, username, and role
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}
