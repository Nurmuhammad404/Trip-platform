package com.epam.trip.exception;

public class EntityNotFoundException extends TripPlatformException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}