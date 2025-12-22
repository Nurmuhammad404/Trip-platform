package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Car;
import com.epam.trip.service.CarService;

import java.util.List;
import java.util.stream.Collectors;

public class CarServiceImpl implements CarService {
    private final GenericDao<Car> carDao;

    public CarServiceImpl() {
        this.carDao = DaoFactory.getCarDao();
    }

    public CarServiceImpl(GenericDao<Car> carDao) {
        this.carDao = carDao;
    }

    @Override
    public Car findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        return carDao.findById(id);
    }

    @Override
    public List<Car> findAll() {
        return carDao.findAll();
    }

    @Override
    public void save(Car car) {
        validateCar(car);
        carDao.save(car);
    }

    @Override
    public void update(Car car) {
        if (car.getId() == null) {
            throw new IllegalArgumentException("Car ID cannot be null for update");
        }
        validateCar(car);
        carDao.update(car);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        carDao.delete(id);
    }

    public List<Car> searchByType(String type) {
        return carDao.findAll().stream()
                .filter(c -> c.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Car> searchByLocation(String location) {
        return carDao.findAll().stream()
                .filter(c -> c.getLocation().equalsIgnoreCase(location))
                .collect(Collectors.toList());
    }

    public List<Car> getAvailableCars() {
        return carDao.findAll().stream()
                .filter(Car::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Car> searchByPriceRange(double minPrice, double maxPrice) {
        return carDao.findAll().stream()
                .filter(c -> c.getPricePerDay() >= minPrice && c.getPricePerDay() <= maxPrice)
                .collect(Collectors.toList());
    }

    public boolean rentCar(Long carId) {
        Car car = findById(carId);
        if (car != null && car.isAvailable()) {
            car.setAvailable(false);
            carDao.update(car);
            return true;
        }
        return false;
    }

    public boolean returnCar(Long carId) {
        Car car = findById(carId);
        if (car != null && !car.isAvailable()) {
            car.setAvailable(true);
            carDao.update(car);
            return true;
        }
        return false;
    }

    private void validateCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (car.getPricePerDay() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}