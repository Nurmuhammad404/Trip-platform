package com.epam.trip.exception;

public class DaoException extends TripPlatformException {
    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}