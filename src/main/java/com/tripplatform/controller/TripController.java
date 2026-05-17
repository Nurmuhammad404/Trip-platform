package com.tripplatform.controller;

import com.tripplatform.model.Trip;
import com.tripplatform.service.TripService;
import java.util.List;

public class TripController {
    private TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    public void createTrip(Trip trip) {
        tripService.createTrip(trip);
    }

    public Trip getTrip(int id) {
        return tripService.getTrip(id);
    }

    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    public void deleteTrip(int id) {
        tripService.deleteTrip(id);
    }
}