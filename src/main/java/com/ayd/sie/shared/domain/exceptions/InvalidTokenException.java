package com.ayd.sie.shared.domain.exceptions;

public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException(String message) {
        super(message);
    }
}