package com.ayd.sie.shared.infrastructure.web;

import com.ayd.sie.shared.domain.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
                        InvalidCredentialsException ex, WebRequest request) {
                log.warn("Invalid credentials attempt: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "INVALID_CREDENTIALS",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(AccountLockedException.class)
        public ResponseEntity<Map<String, Object>> handleAccountLocked(
                        AccountLockedException ex, WebRequest request) {
                log.warn("Account locked attempt: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "ACCOUNT_LOCKED",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(TwoFactorRequiredException.class)
        public ResponseEntity<Map<String, Object>> handleTwoFactorRequired(
                        TwoFactorRequiredException ex, WebRequest request) {
                log.info("Two-factor authentication required: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "TWO_FACTOR_REQUIRED",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidToken(
                        InvalidTokenException ex, WebRequest request) {
                log.warn("Invalid token: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "INVALID_TOKEN",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleResourceNotFound(
                        ResourceNotFoundException ex, WebRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "RESOURCE_NOT_FOUND",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // ResourceNotFoundException
        @ExceptionHandler(BusinessConstraintViolationException.class)
        public ResponseEntity<Map<String, Object>> handleBusinessConstraint(
                        BusinessConstraintViolationException ex, WebRequest request) {
                log.warn("Business constraint violation: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "BUSINESS_CONSTRAINT_VIOLATION",
                                ex.getMessage(),
                                request);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Map<String, Object>> handleAccessDenied(
                        AccessDeniedException ex, WebRequest request) {
                log.warn("Access denied: {}", ex.getMessage());

                Map<String, Object> errorResponse = createErrorResponse(
                                "ACCESS_DENIED",
                                "You don't have permission to access this resource",
                                request);

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        validationErrors.put(fieldName, errorMessage);
                });

                Map<String, Object> errorResponse = createErrorResponse(
                                "VALIDATION_ERROR",
                                "Validation failed for one or more fields",
                                request);
                errorResponse.put("validation_errors", validationErrors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGenericException(
                        Exception ex, WebRequest request) {
                log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

                Map<String, Object> errorResponse = createErrorResponse(
                                "INTERNAL_SERVER_ERROR",
                                "An unexpected error occurred. Please try again later.",
                                request);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        private Map<String, Object> createErrorResponse(String errorCode, String message, WebRequest request) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error_code", errorCode);
                errorResponse.put("message", message);
                errorResponse.put("timestamp", System.currentTimeMillis());
                errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
                return errorResponse;
        }

        @ExceptionHandler(UserHasDependenciesException.class)
        public ResponseEntity<Map<String, Object>> handleUserHasDependenciesException(UserHasDependenciesException ex) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "USER_HAS_DEPENDENCIES");
                response.put("message", ex.getMessage());
                response.put("timestamp", System.currentTimeMillis());

                if (ex.getReferences() != null) {
                        response.put("references", ex.getReferences());
                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
}