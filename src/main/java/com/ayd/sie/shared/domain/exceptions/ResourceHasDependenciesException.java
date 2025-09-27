package com.ayd.sie.shared.domain.exceptions;

import com.ayd.sie.admin.application.dto.UserReferencesDto;
import lombok.Getter;

@Getter
public class ResourceHasDependenciesException extends RuntimeException {

    private final UserReferencesDto references;

    public ResourceHasDependenciesException(String message) {
        super(message);
        this.references = null;
    }

    public ResourceHasDependenciesException(String message, UserReferencesDto references) {
        super(message);
        this.references = references;
    }
}