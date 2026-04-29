package com.ws101.tomacas.EcommerceApi.controller;

import com.ws101.tomacas.EcommerceApi.model.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for the E-commerce API.
 *
 * Catches common exceptions across all controllers and returns
 * a consistent {@link ErrorResponse} format with timestamp,
 * status code, error label, and descriptive message.
 *
 * <p>This handler covers the following scenarios:</p>
 * <ul>
 *   <li>Product not found (404)</li>
 *   <li>JPA entity not found (404)</li>
 *   <li>Invalid request body or malformed JSON (400)</li>
 *   <li>Database constraint violations (400)</li>
 *   <li>Invalid method arguments or path variables (400)</li>
 *   <li>Bean Validation constraint violations (400)</li>
 *   <li>Access denied / forbidden (403)</li>
 *   <li>Unexpected server errors (500)</li>
 * </ul>
 *
 * @author Jules Ian C. Tomacas
 * @see ErrorResponse
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested product does not exist.
     *
     * @param ex the exception thrown when a product is not found
     * @return a 404 response with error details
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles JPA EntityNotFoundException when a database lookup fails.
     *
     * @param ex the exception thrown by the persistence layer
     * @return a 404 response with error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles invalid arguments from service layer validation.
     *
     * @param ex the exception thrown for invalid arguments
     * @return a 400 response with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles database constraint violations (e.g., unique key conflicts,
     * foreign key violations, or NOT NULL constraint failures).
     *
     * @param ex the exception thrown by the database layer
     * @return a 400 response indicating a data integrity issue
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Data integrity violation: the request conflicts with existing data or constraints."
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed or unreadable JSON request bodies.
     *
     * @param ex the exception thrown when request body cannot be parsed
     * @return a 400 response indicating the request body is invalid
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Request body is missing or contains invalid JSON."
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch errors in path variables or request params.
     * For example, passing a string where a Long ID is expected.
     *
     * @param ex the exception thrown for type mismatches
     * @return a 400 response indicating the parameter type is wrong
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value for parameter '" + ex.getName()
                + "': expected type " + expectedType;
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch-all handler for any unexpected exceptions not covered
     * by more specific handlers above.
     *
     * @param ex the unexpected exception
     * @return a 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns the current timestamp formatted as ISO-8601 string.
     *
     * @return formatted timestamp string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Handles Bean Validation errors thrown when {@code @Valid} fails.
     *
     * <p>Extracts individual field error messages and returns them
     * as a structured JSON response with status 400 Bad Request.</p>
     *
     * @param ex the validation exception containing field errors
     * @return a 400 response with a list of validation error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "Field '" + fieldError.getField()
                        + "': " + fieldError.getDefaultMessage())
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", getCurrentTimestamp());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied exceptions (403 Forbidden).
     *
     * <p>Thrown when an authenticated user tries to access a
     * resource they do not have permission for.</p>
     *
     * @param ex the access denied exception
     * @return a 403 response with error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                getCurrentTimestamp(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have permission to access this resource."
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
