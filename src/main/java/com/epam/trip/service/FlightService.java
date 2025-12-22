package com.epam.trip.service;

import com.epam.trip.entity.Flight;
import java.util.List;

public interface FlightService {
    Flight findById(Long id);
    List<Flight> findAll();
    void save(Flight flight);
    void update(Flight flight);
    void delete(Long id);
    // Additional business methods
}