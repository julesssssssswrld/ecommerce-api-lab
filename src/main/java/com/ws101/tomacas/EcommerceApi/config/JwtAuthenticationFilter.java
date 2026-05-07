package com.ws101.tomacas.EcommerceApi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ws101.tomacas.EcommerceApi.service.CustomUserDetailsService;

import java.io.IOException;

/**
 * JWT authentication filter that intercepts every HTTP request.
 *
 * <p>This filter replaces Spring Security's default session-based
 * authentication. It extracts the JWT from the {@code Authorization}
 * header, validates it, and sets the security context so that
 * downstream filters and controllers can identify the user.</p>
 *
 * <p>If no token is present or the token is invalid, the filter
 * simply passes the request along — Spring Security's authorization
 * rules will then decide whether to allow or deny access.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see JwtUtil
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructs the filter with required dependencies.
     *
     * @param jwtUtil            the JWT utility for token operations
     * @param userDetailsService the service to load user details from the database
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters each request to check for a valid JWT token.
     *
     * <p>Processing steps:</p>
     * <ol>
     *   <li>Extract the JWT from the {@code Authorization: Bearer ...} header</li>
     *   <li>If absent or malformed, pass through without authentication</li>
     *   <li>Extract the username from the token</li>
     *   <li>Load the user details from the database</li>
     *   <li>Validate the token (signature + expiration + username match)</li>
     *   <li>If valid, set the authentication in the SecurityContext</li>
     * </ol>
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract the JWT token from the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " prefix to get the actual token
        jwt = authHeader.substring(7);

        try {
            // 2. Extract the username from the token
            username = jwtUtil.extractUsername(jwt);

            // 3. Only authenticate if user is not already in the security context
            if (StringUtils.hasText(username) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Validate the token against the loaded user details
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token is invalid or expired — log and let the request proceed
            // without authentication (Spring Security will deny if needed)
            logger.error("Error processing JWT token", e);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
