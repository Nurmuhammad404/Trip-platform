package com.tripplatform.repository;

import com.tripplatform.model.Trip;
import java.util.List;
import java.util.ArrayList;

public class TripRepositoryImpl implements TripRepository {
    private List<Trip> trips = new ArrayList<>();

    @Override
    public void save(Trip trip) {
        trips.add(trip);
    }

    @Override
    public Trip findById(int id) {
        return trips.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Trip> findAll() {
        return new ArrayList<>(trips);
    }

    @Override
    public void delete(int id) {
        trips.removeIf(t -> t.getId() == id);
    }
}