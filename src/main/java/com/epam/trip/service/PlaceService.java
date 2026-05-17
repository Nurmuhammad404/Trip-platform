package com.epam.trip.service;

import com.epam.trip.entity.Place;
import java.util.List;

public interface PlaceService {
    Place findById(Long id);
    List<Place> findAll();
    void save(Place place);
    void update(Place place);
    void delete(Long id);
    // Additional business methods
}