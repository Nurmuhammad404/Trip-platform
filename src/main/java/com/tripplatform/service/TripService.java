package com.tripplatform.service;

import com.tripplatform.model.Trip;
import java.util.List;

public interface TripService {
    void createTrip(Trip trip);
    Trip getTrip(int id);
    List<Trip> getAllTrips();
    void updateTrip(Trip trip);
    void deleteTrip(int id);
}