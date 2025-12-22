package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Taxi;
import com.epam.trip.service.TaxiService;

import java.util.List;
import java.util.stream.Collectors;

public class TaxiServiceImpl implements TaxiService {
    private final GenericDao<Taxi> taxiDao;

    public TaxiServiceImpl() {
        this.taxiDao = DaoFactory.getTaxiDao();
    }

    public TaxiServiceImpl(GenericDao<Taxi> taxiDao) {
        this.taxiDao = taxiDao;
    }

    @Override
    public Taxi findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid taxi ID");
        }
        return taxiDao.findById(id);
    }

    @Override
    public List<Taxi> findAll() {
        return taxiDao.findAll();
    }

    @Override
    public void save(Taxi taxi) {
        validateTaxi(taxi);
        taxiDao.save(taxi);
    }

    @Override
    public void update(Taxi taxi) {
        if (taxi.getId() == null) {
            throw new IllegalArgumentException("Taxi ID cannot be null for update");
        }
        validateTaxi(taxi);
        taxiDao.update(taxi);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid taxi ID");
        }
        taxiDao.delete(id);
    }

    public List<Taxi> searchByCity(String city) {
        return taxiDao.findAll().stream()
                .filter(t -> t.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Taxi> getAvailableTaxis() {
        return taxiDao.findAll().stream()
                .filter(Taxi::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Taxi> getTopRated(double minRating) {
        return taxiDao.findAll().stream()
                .filter(t -> t.getRating() >= minRating)
                .sorted((t1, t2) -> Double.compare(t2.getRating(), t1.getRating()))
                .collect(Collectors.toList());
    }

    public boolean bookTaxi(Long taxiId) {
        Taxi taxi = findById(taxiId);
        if (taxi != null && taxi.isAvailable()) {
            taxi.setAvailable(false);
            taxiDao.update(taxi);
            return true;
        }
        return false;
    }

    private void validateTaxi(Taxi taxi) {
        if (taxi == null) {
            throw new IllegalArgumentException("Taxi cannot be null");
        }
        if (taxi.getDriverName() == null || taxi.getDriverName().trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name is required");
        }
        if (taxi.getLicensePlate() == null || taxi.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }
        if (taxi.getPricePerKm() < 0) {
            throw new IllegalArgumentException("Price per km cannot be negative");
        }
        if (taxi.getRating() < 0 || taxi.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
}