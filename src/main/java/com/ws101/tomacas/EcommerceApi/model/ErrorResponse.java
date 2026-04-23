package com.ws101.tomacas.EcommerceApi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Standard error response model for consistent API error formatting.
 *
 * Provides a uniform structure for all error responses returned
 * by the API, including a timestamp, HTTP status code, and a
 * human-readable error message.
 *
 * @author Jules Ian C. Tomacas
 * @author Jovan P. Atencio
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** ISO-8601 timestamp of when the error occurred. */
    private String timestamp;

    /** HTTP status code (e.g., 404, 400, 500). */
    private int status;

    /** Short error type label (e.g., "Not Found", "Bad Request"). */
    private String error;

    /** Detailed, human-readable description of what went wrong. */
    private String message;
}
