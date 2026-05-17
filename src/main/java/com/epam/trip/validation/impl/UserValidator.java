package com.epam.trip.validation.impl;

import com.epam.trip.entity.User;
import com.epam.trip.validation.ValidationResult;
import com.epam.trip.validation.Validator;

public class UserValidator implements Validator<User> {
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    public ValidationResult validate(User user) {
        ValidationResult result = ValidationResult.ok();
        if (user == null) return result.addError("User cannot be null");

        if (isBlank(user.getUsername()))
            result.addError("Username is required");
        else if (user.getUsername().length() < 3)
            result.addError("Username must be at least 3 characters");

        if (isBlank(user.getEmail()))
            result.addError("Email is required");
        else if (!user.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$"))
            result.addError("Email is not valid");

        if (isBlank(user.getPassword()))
            result.addError("Password is required");
        else if (user.getPassword().length() < MIN_PASSWORD_LENGTH)
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");

        return result;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
