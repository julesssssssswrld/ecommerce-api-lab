package com.ws101.tomacas.EcommerceApi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for Cross-Origin Resource Sharing (CORS).
 *
 * <p>Allows the frontend application (running on a different origin,
 * such as localhost:5500 via Live Server) to communicate with this
 * backend API. Without this configuration, the browser would block
 * cross-origin requests due to the Same-Origin Policy.</p>
 *
 * <p>This configuration permits:</p>
 * <ul>
 *   <li>Origins: {@code http://localhost:5500} and {@code http://127.0.0.1:5500}</li>
 *   <li>Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS</li>
 *   <li>Headers: Authorization, Content-Type</li>
 * </ul>
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures global CORS mappings for all API endpoints.
     *
     * <p>Registers CORS rules that allow the frontend to make
     * requests to any endpoint under {@code /api/**}. The allowed
     * origins correspond to the ports commonly used by VS Code
     * Live Server and similar local development servers.</p>
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
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);
    }
}
