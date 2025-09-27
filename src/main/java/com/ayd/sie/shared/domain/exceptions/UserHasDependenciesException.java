package com.ayd.sie.shared.domain.exceptions;

import com.ayd.sie.admin.application.dto.UserReferencesDto;
import lombok.Getter;

@Getter
public class UserHasDependenciesException extends RuntimeException {

    private final UserReferencesDto references;

    public UserHasDependenciesException(String message) {
        super(message);
        this.references = null;
    }

    public UserHasDependenciesException(String message, UserReferencesDto references) {
        super(message);
        this.references = references;
    }
}