package com.epam.trip.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public ValidationResult addError(String message) {
        errors.add(message);
        return this;
    }

    public boolean isValid() { return errors.isEmpty(); }

    public List<String> getErrors() { return Collections.unmodifiableList(errors); }

    public static ValidationResult ok() { return new ValidationResult(); }
}
