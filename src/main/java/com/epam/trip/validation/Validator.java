package com.epam.trip.validation;

/**
 * Generic validator interface.
 * Implementations validate a specific entity type and return a ValidationResult.
 */
public interface Validator<T> {
    ValidationResult validate(T entity);

    default void validateOrThrow(T entity) {
        ValidationResult result = validate(entity);
        if (!result.isValid()) {
            throw new com.epam.trip.exception.ValidationException(result.getErrors().toString());
        }
    }
}
