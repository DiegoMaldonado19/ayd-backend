package com.ayd.sie.shared.domain.exceptions;

public class BusinessConstraintViolationException extends RuntimeException {

    public BusinessConstraintViolationException(String message) {
        super(message);
    }

    public BusinessConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}