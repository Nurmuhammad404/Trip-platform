package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Flight;
import com.epam.trip.service.FlightService;

import java.util.List;
import java.util.stream.Collectors;

public class FlightServiceImpl implements FlightService {
    private final GenericDao<Flight> flightDao;

    public FlightServiceImpl() {
        this.flightDao = DaoFactory.getFlightDao();
    }

    public FlightServiceImpl(GenericDao<Flight> flightDao) {
        this.flightDao = flightDao;
    }

    @Override
    public Flight findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid flight ID");
        }
        return flightDao.findById(id);
    }

    @Override
    public List<Flight> findAll() {
        return flightDao.findAll();
    }

    @Override
    public void save(Flight flight) {
        validateFlight(flight);
        flightDao.save(flight);
    }

    @Override
    public void update(Flight flight) {
        if (flight.getId() == null) {
            throw new IllegalArgumentException("Flight ID cannot be null for update");
        }
        validateFlight(flight);
        flightDao.update(flight);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid flight ID");
        }
        flightDao.delete(id);
    }

    public List<Flight> searchByRoute(String departure, String destination) {
        return flightDao.findAll().stream()
                .filter(f -> f.getDeparture().equalsIgnoreCase(departure)
                        && f.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    public List<Flight> searchByDeparture(String departure) {
        return flightDao.findAll().stream()
                .filter(f -> f.getDeparture().equalsIgnoreCase(departure))
                .collect(Collectors.toList());
    }

    public List<Flight> searchByDestination(String destination) {
        return flightDao.findAll().stream()
                .filter(f -> f.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    public List<Flight> getAvailableFlights() {
        return flightDao.findAll().stream()
                .filter(f -> f.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public boolean bookSeat(Long flightId) {
        Flight flight = findById(flightId);
        if (flight != null && flight.getAvailableSeats() > 0) {
            flight.setAvailableSeats(flight.getAvailableSeats() - 1);
            flightDao.update(flight);
            return true;
        }
        return false;
    }

    private void validateFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number is required");
        }
        if (flight.getDeparture() == null || flight.getDeparture().trim().isEmpty()) {
            throw new IllegalArgumentException("Departure is required");
        }
        if (flight.getDestination() == null || flight.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination is required");
        }
        if (flight.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (flight.getAvailableSeats() < 0) {
            throw new IllegalArgumentException("Available seats cannot be negative");
        }
    }
}