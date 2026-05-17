package com.tripplatform.repository;

import com.tripplatform.model.Trip;
import java.util.List;

public interface TripRepository {
    void save(Trip trip);
    Trip findById(int id);
    List<Trip> findAll();
    void delete(int id);
}