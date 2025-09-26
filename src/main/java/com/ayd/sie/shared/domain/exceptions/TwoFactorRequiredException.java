package com.ayd.sie.shared.domain.exceptions;

public class TwoFactorRequiredException extends AuthenticationException {
    public TwoFactorRequiredException(String message) {
        super(message);
    }
}