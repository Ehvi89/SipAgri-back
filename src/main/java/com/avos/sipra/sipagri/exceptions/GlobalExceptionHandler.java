package com.avos.sipra.sipagri.exceptions;

import com.avos.sipra.sipagri.services.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler is a centralized exception handling class that intercepts exceptions
 * thrown by controllers in the application and provides well-structured HTTP responses for
 * different types of exceptions.
 * <p>
 * The class uses the Spring Framework's @RestControllerAdvice annotation to mark it as a
 * global exception handler. Specific exception handler methods are provided to handle various
 * types of exceptions and return appropriate HTTP status codes along with a custom error response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type {@code ResourceNotFoundException} that are thrown when
     * a requested resource cannot be found. Constructs an {@code ApiResponse} object
     * with failure status, null data, and the exception message, and returns it as
     * the response body with an HTTP 404 (Not Found) status code.
     *
     * @param ex the {@code ResourceNotFoundException} instance containing details of
     *           the resource that could not be found.
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} object as
     *         the response body with the HTTP status set to 404 (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                false,
                null,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions of type {@code MethodArgumentNotValidException} that are thrown
     * when validation on a method argument annotated with {@code @Valid} fails.
     * <p>
     * The method collects all validation errors, maps field names to their corresponding
     * error messages, and wraps them in an {@code ApiResponse} object. It then returns
     * a {@code ResponseEntity} with a {@code BAD_REQUEST} status.
     *
     * @param ex the {@code MethodArgumentNotValidException} containing details of validation errors
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} object with validation errors
     *         mapped to their respective field names and an appropriate error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                errors,
                "Erreurs de validation"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link IllegalArgumentException} and returns a structured error response with
     * HTTP status code 400 (BAD_REQUEST).
     *
     * @param ex the {@link IllegalArgumentException} instance that was thrown
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with the error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                false,
                null,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles a generic exception in the application and returns a standardized response
     * with HTTP status 500 (INTERNAL_SERVER_ERROR).
     *
     * @param ex the exception that occurred and needs to be handled
     * @return a ResponseEntity containing an ApiResponse with an error message and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                false,
                null,
                "Une erreur interne s'est produite: " + ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
