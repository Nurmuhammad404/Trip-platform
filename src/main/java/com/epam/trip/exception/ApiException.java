package com.epam.trip.exception;

/**
 * Exception thrown when API operations fail.
 * This includes network errors, invalid responses, rate limiting, etc.
 */
public class ApiException extends TripPlatformException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create exception for HTTP error responses
     */
    public static ApiException httpError(int statusCode, String message) {
        return new ApiException(String.format("HTTP %d: %s", statusCode, message));
    }

    /**
     * Create exception for network failures
     */
    public static ApiException networkError(String message, Throwable cause) {
        return new ApiException("Network error: " + message, cause);
    }

    /**
     * Create exception for rate limiting
     */
    public static ApiException rateLimited(String message) {
        return new ApiException("Rate limit exceeded: " + message);
    }

    /**
     * Create exception for invalid JSON responses
     */
    public static ApiException invalidResponse(String message) {
        return new ApiException("Invalid API response: " + message);
    }
}
