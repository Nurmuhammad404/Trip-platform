package com.epam.trip.exception;

public class TripPlatformException extends Exception {
    public TripPlatformException(String message) {
        super(message);
    }

    public TripPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}