package com.epam.trip.service;

import com.epam.trip.entity.Car;
import java.util.List;

public interface CarService {
    Car findById(Long id);
    List<Car> findAll();
    void save(Car car);
    void update(Car car);
    void delete(Long id);
    // Additional business methods
}