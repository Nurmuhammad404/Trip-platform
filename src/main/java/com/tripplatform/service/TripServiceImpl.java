package com.tripplatform.service;

import com.tripplatform.model.Trip;
import com.tripplatform.repository.TripRepository;
import java.util.List;

public class TripServiceImpl implements TripService {
    private TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public void createTrip(Trip trip) {
        tripRepository.save(trip);
    }

    @Override
    public Trip getTrip(int id) {
        return tripRepository.findById(id);
    }

    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public void updateTrip(Trip trip) {
        // TODO: implement update logic
    }

    @Override
    public void deleteTrip(int id) {
        tripRepository.delete(id);
    }
}