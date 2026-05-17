package com.epam.trip.validation;

import com.epam.trip.entity.Flight;
import com.epam.trip.validation.impl.FlightValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightValidatorTest {

    FlightValidator validator;

    @BeforeEach
    void setUp() { validator = new FlightValidator(); }

    @Test
    void validate_validFlight_noErrors() {
        Flight f = buildFlight("AA101", "New York", "Los Angeles", 299.99, 50);
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
    }

    @Test
    void validate_missingFlightNumber_hasError() {
        Flight f = buildFlight(null, "New York", "Los Angeles", 100.0, 10);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("Flight number")));
    }

    @Test
    void validate_sameDepartureAndDestination_hasError() {
        Flight f = buildFlight("AA101", "Paris", "Paris", 100.0, 10);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("same")));
    }

    @Test
    void validate_negativePrice_hasError() {
        Flight f = buildFlight("AA101", "NYC", "LA", -50.0, 10);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("Price")));
    }

    @Test
    void validate_negativeSeats_hasError() {
        Flight f = buildFlight("AA101", "NYC", "LA", 100.0, -1);
        assertFalse(validator.validate(f).isValid());
    }

    @Test
    void validate_nullFlight_hasError() {
        assertFalse(validator.validate(null).isValid());
    }

    private Flight buildFlight(String num, String dep, String dest, double price, int seats) {
        Flight f = new Flight();
        f.setFlightNumber(num);
        f.setDeparture(dep);
        f.setDestination(dest);
        f.setPrice(price);
        f.setAvailableSeats(seats);
        return f;
    }
}
