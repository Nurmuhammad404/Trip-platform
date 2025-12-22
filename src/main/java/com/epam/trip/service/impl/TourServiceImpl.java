package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Tour;
import com.epam.trip.service.TourService;

import java.util.List;
import java.util.stream.Collectors;

public class TourServiceImpl implements TourService {
    private final GenericDao<Tour> tourDao;

    public TourServiceImpl() {
        this.tourDao = DaoFactory.getTourDao();
    }

    public TourServiceImpl(GenericDao<Tour> tourDao) {
        this.tourDao = tourDao;
    }

    @Override
    public Tour findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid tour ID");
        }
        return tourDao.findById(id);
    }

    @Override
    public List<Tour> findAll() {
        return tourDao.findAll();
    }

    @Override
    public void save(Tour tour) {
        validateTour(tour);
        tourDao.save(tour);
    }

    @Override
    public void update(Tour tour) {
        if (tour.getId() == null) {
            throw new IllegalArgumentException("Tour ID cannot be null for update");
        }
        validateTour(tour);
        tourDao.update(tour);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid tour ID");
        }
        tourDao.delete(id);
    }

    public List<Tour> searchByDestination(String destination) {
        return tourDao.findAll().stream()
                .filter(t -> t.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Tour> searchByDuration(int maxDuration) {
        return tourDao.findAll().stream()
                .filter(t -> t.getDuration() <= maxDuration)
                .collect(Collectors.toList());
    }

    public List<Tour> searchByPriceRange(double minPrice, double maxPrice) {
        return tourDao.findAll().stream()
                .filter(t -> t.getPrice() >= minPrice && t.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    private void validateTour(Tour tour) {
        if (tour == null) {
            throw new IllegalArgumentException("Tour cannot be null");
        }
        if (tour.getName() == null || tour.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tour name is required");
        }
        if (tour.getDestination() == null || tour.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination is required");
        }
        if (tour.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (tour.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}