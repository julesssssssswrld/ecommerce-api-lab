package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.config.JwtUtil;
import com.ws101.tomacas.EcommerceApi.dto.LoginRequestDto;
import com.ws101.tomacas.EcommerceApi.dto.RegisterUserDto;
import com.ws101.tomacas.EcommerceApi.model.User;
import com.ws101.tomacas.EcommerceApi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 *
 * <p>Provides endpoints for user registration, JWT-based login,
 * and retrieving the currently authenticated user's information.
 * Login returns a JWT token that the client must include in
 * subsequent requests via the {@code Authorization: Bearer} header.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see AuthService
 * @see JwtUtil
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param authService           the service handling registration logic
     * @param authenticationManager the manager for credential verification
     * @param jwtUtil               the utility for JWT token generation
     */
    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * <p>Accepts a JSON body with {@code username} and {@code password}.
     * On successful authentication, generates a signed JWT containing
     * the user's identity and returns it in the response body.</p>
     *
     * @param loginRequest the login credentials
     * @return a JSON object containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequestDto loginRequest) {

        try {
            // Authenticate credentials against the database
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT token for the authenticated user
            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);

            Map<String, Object> response = Map.of(
                    "token", token,
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "message", "Login successful"
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
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
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterUserDto dto) {
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
     * logged-in user directly from the security context (populated
     * by the JWT filter). Returns 401 if no valid token was provided.</p>
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
