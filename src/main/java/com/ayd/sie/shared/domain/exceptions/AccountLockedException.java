package com.ayd.sie.shared.domain.exceptions;

public class AccountLockedException extends AuthenticationException {
    public AccountLockedException(String message) {
        super(message);
    }
}