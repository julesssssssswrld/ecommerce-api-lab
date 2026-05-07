package com.ws101.tomacas.EcommerceApi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for Cross-Origin Resource Sharing (CORS).
 *
 * <p>Allows the frontend application (running on a different origin,
 * such as localhost:5500 via Live Server) to communicate with this
 * backend API. Configured for JWT authentication — no cookies are
 * needed, so {@code allowCredentials} is set to {@code false}.</p>
 *
 * <p>The {@code Authorization} header is exposed so the frontend
 * can send JWT Bearer tokens with each request.</p>
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures global CORS mappings for all API endpoints.
     *
     * <p>Since JWT authentication uses the {@code Authorization} header
     * instead of cookies, we no longer need separate CORS entries for
     * {@code /login} and {@code /logout}. All auth goes through
     * {@code /api/**} endpoints.</p>
     *
     * @param registry the CORS registry to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "http://localhost:5501",
                        "http://127.0.0.1:5501"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(false);
    }
}
