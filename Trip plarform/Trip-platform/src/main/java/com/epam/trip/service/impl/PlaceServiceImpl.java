package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Place;
import com.epam.trip.service.PlaceService;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceServiceImpl implements PlaceService {
    private final GenericDao<Place> placeDao;

    public PlaceServiceImpl() {
        this.placeDao = DaoFactory.getPlaceDao();
    }

    public PlaceServiceImpl(GenericDao<Place> placeDao) {
        this.placeDao = placeDao;
    }

    @Override
    public Place findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid place ID");
        }
        return placeDao.findById(id);
    }

    @Override
    public List<Place> findAll() {
        return placeDao.findAll();
    }

    @Override
    public void save(Place place) {
        validatePlace(place);
        placeDao.save(place);
    }

    @Override
    public void update(Place place) {
        if (place.getId() == null) {
            throw new IllegalArgumentException("Place ID cannot be null for update");
        }
        validatePlace(place);
        placeDao.update(place);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid place ID");
        }
        placeDao.delete(id);
    }

    public List<Place> searchByCity(String city) {
        return placeDao.findAll().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Place> searchByCountry(String country) {
        return placeDao.findAll().stream()
                .filter(p -> p.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public List<Place> searchByCategory(String category) {
        return placeDao.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Place> getTopRated(double minRating) {
        return placeDao.findAll().stream()
                .filter(p -> p.getRating() >= minRating)
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .collect(Collectors.toList());
    }

    public List<Place> getFreePlaces() {
        return placeDao.findAll().stream()
                .filter(p -> p.getEntryFee() == 0)
                .collect(Collectors.toList());
    }

    private void validatePlace(Place place) {
        if (place == null) {
            throw new IllegalArgumentException("Place cannot be null");
        }
        if (place.getName() == null || place.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Place name is required");
        }
        if (place.getCity() == null || place.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (place.getRating() < 0 || place.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        if (place.getEntryFee() < 0) {
            throw new IllegalArgumentException("Entry fee cannot be negative");
        }
    }
}