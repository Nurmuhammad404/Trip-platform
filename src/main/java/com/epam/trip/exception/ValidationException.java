package com.epam.trip.exception;

public class ValidationException extends TripPlatformException {
    public ValidationException(String message) {
        super(message);
    }
}