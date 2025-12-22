package com.epam.trip.exception;

public class ServiceException extends TripPlatformException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}