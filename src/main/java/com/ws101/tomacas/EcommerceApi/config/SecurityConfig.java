package com.ws101.tomacas.EcommerceApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security configuration for stateless JWT authentication.
 *
 * <p>Configures the security filter chain to use JWT tokens instead
 * of server-side sessions. Key differences from session-based auth:</p>
 * <ul>
 *   <li>CSRF is disabled (stateless APIs don't need it)</li>
 *   <li>Sessions are set to STATELESS (no JSESSIONID cookies)</li>
 *   <li>A custom {@link JwtAuthenticationFilter} runs before the
 *       default {@link UsernamePasswordAuthenticationFilter}</li>
 *   <li>Login is handled via a REST endpoint, not form login</li>
 * </ul>
 *
 * @author Jules Ian C. Tomacas
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs the security configuration with required dependencies.
     *
     * @param jwtAuthFilter      the JWT filter to register in the chain
     * @param userDetailsService the service for loading user details
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Creates a BCrypt password encoder bean.
     *
     * <p>BCrypt is a strong adaptive hashing algorithm recommended
     * by Spring Security. It automatically generates and stores
     * a salt with each hash.</p>
     *
     * @return a BCrypt-based password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the authentication manager bean for programmatic use.
     *
     * <p>Required by the login endpoint to authenticate credentials
     * before issuing a JWT token.</p>
     *
     * @param config the authentication configuration
     * @return the configured authentication manager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the authentication provider with our user details
     * service and password encoder.
     *
     * @return the configured DAO authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures the HTTP security filter chain for JWT authentication.
     *
     * <p>Key configuration:</p>
     * <ul>
     *   <li><b>Stateless Sessions:</b> {@code SessionCreationPolicy.STATELESS}
     *       ensures no HTTP sessions are created or used</li>
     *   <li><b>CSRF Disabled:</b> Not needed for stateless JWT APIs</li>
     *   <li><b>JWT Filter:</b> Runs before {@code UsernamePasswordAuthenticationFilter}
     *       to authenticate requests via Bearer tokens</li>
     *   <li><b>Public Endpoints:</b> Login, register, and GET product
     *       endpoints are accessible without a token</li>
     * </ul>
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Enable CORS using the WebConfig CORS mappings
            .cors(Customizer.withDefaults())

            // Disable CSRF — stateless JWT APIs don't need CSRF protection
            .csrf(csrf -> csrf.disable())

            // Configure session management to be stateless (no HTTP sessions)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public: anyone can view products
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

                // Public: registration and login endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

                // Admin only: delete products
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")

                // Admin and Seller: create and update products
                .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Set the authentication provider
            .authenticationProvider(authenticationProvider())

            // Add JWT filter before the default UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // Exception handling: return 401 JSON instead of redirect
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                )
            );

        return http.build();
    }
}
