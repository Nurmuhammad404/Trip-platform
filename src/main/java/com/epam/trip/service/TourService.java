package com.epam.trip.service;

import com.epam.trip.entity.Tour;
import java.util.List;

public interface TourService {
    Tour findById(Long id);
    List<Tour> findAll();
    void save(Tour tour);
    void update(Tour tour);
    void delete(Long id);
    // Additional business methods
}