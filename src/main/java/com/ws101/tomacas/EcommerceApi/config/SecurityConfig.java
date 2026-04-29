package com.ws101.tomacas.EcommerceApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security configuration for session-based authentication.
 *
 * <p>Configures the security filter chain with the following rules:</p>
 * <ul>
 *   <li>Public endpoints: GET products, register, login page</li>
 *   <li>Admin-only: DELETE products, user management</li>
 *   <li>Authenticated: orders, account info</li>
 *   <li>CSRF protection enabled with cookie-based token repository</li>
 *   <li>Session-based auth using form login</li>
 * </ul>
 *
 * @author Jules Ian C. Tomacas
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

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
     * Configures the HTTP security filter chain.
     *
     * <p>Defines which endpoints are public, which require
     * authentication, and which require specific roles. Enables
     * form login and CSRF protection with a cookie-based token
     * for compatibility with frontend JavaScript.</p>
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Use a request handler that does not require the CSRF token
        // to be resolved before the request body is read, which is
        // needed for SPAs sending JSON with fetch()
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        http
            // Enable CORS using the WebConfig CORS mappings
            .cors(Customizer.withDefaults())

            // CSRF protection with cookie-based token for frontend JS
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public: anyone can view products
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

                // Public: registration endpoint
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()

                // Public: login and logout pages
                .requestMatchers("/login", "/logout").permitAll()

                // Admin only: delete products and manage users
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")

                // Admin and Seller: create and update products
                .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/products/**").hasAnyRole("ADMIN", "SELLER")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Exception handling for REST API
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                )
            )

            // Enable form login with REST-friendly success/failure handlers
            .formLogin(form -> form
                .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed"))
            )

            // Enable logout with session invalidation
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            // Session management: create session when needed
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }
}
