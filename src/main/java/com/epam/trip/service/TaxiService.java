package com.epam.trip.service;

import com.epam.trip.entity.Taxi;
import java.util.List;

public interface TaxiService {
    Taxi findById(Long id);
    List<Taxi> findAll();
    void save(Taxi taxi);
    void update(Taxi taxi);
    void delete(Long id);
    // Additional business methods
}