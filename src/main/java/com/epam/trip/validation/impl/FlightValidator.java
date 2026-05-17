package com.epam.trip.validation.impl;

import com.epam.trip.entity.Flight;
import com.epam.trip.validation.ValidationResult;
import com.epam.trip.validation.Validator;

public class FlightValidator implements Validator<Flight> {

    @Override
    public ValidationResult validate(Flight f) {
        ValidationResult result = ValidationResult.ok();
        if (f == null) return result.addError("Flight cannot be null");

        if (isBlank(f.getFlightNumber())) result.addError("Flight number is required");
        if (isBlank(f.getDeparture()))    result.addError("Departure is required");
        if (isBlank(f.getDestination()))  result.addError("Destination is required");
        if (f.getDeparture() != null && f.getDeparture().equalsIgnoreCase(f.getDestination()))
            result.addError("Departure and destination cannot be the same");
        if (f.getPrice() < 0)            result.addError("Price cannot be negative");
        if (f.getAvailableSeats() < 0)   result.addError("Available seats cannot be negative");

        return result;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
