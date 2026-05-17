package com.epam.trip.validation.impl;

import com.epam.trip.entity.Hotel;
import com.epam.trip.validation.ValidationResult;
import com.epam.trip.validation.Validator;

public class HotelValidator implements Validator<Hotel> {

    @Override
    public ValidationResult validate(Hotel h) {
        ValidationResult result = ValidationResult.ok();
        if (h == null) return result.addError("Hotel cannot be null");

        if (isBlank(h.getName())) result.addError("Hotel name is required");
        if (isBlank(h.getCity())) result.addError("City is required");
        if (h.getStarRating() < 1 || h.getStarRating() > 5)
            result.addError("Star rating must be between 1 and 5");
        if (h.getPricePerNight() < 0) result.addError("Price per night cannot be negative");
        if (h.getAvailableRooms() < 0) result.addError("Available rooms cannot be negative");

        return result;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
