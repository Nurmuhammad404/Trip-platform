package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.CarCsvSource;
import com.epam.trip.entity.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarDaoImpl implements GenericDao<Car> {
    private final CarCsvSource dataSource;
    private List<Car> cache;

    public CarDaoImpl() {
        this.dataSource = new CarCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public CarDaoImpl(CarCsvSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Car fromCsv(String[] row) {
        if (row.length < 9)
            return null;
        try {
            return new Car(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    Double.parseDouble(row[4]),
                    Boolean.parseBoolean(row[5]),
                    row[6],
                    Integer.parseInt(row[7]),
                    row[8]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Car car) {
        return new String[] {
                car.getId().toString(),
                car.getBrand(),
                car.getModel(),
                car.getType(),
                String.valueOf(car.getPricePerDay()),
                String.valueOf(car.isAvailable()),
                car.getLocation(),
                String.valueOf(car.getSeats()),
                car.getTransmission()
        };
    }

    @Override
    public Car findById(Long id) {
        return cache.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Car save(Car entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(Car::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public Car update(Car entity) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(entity.getId())) {
                cache.set(i, entity);
                saveData();
                return entity;
            }
        }
        return entity;
    }

    @Override
    public void delete(Long id) {
        cache.removeIf(car -> car.getId().equals(id));
        saveData();
    }
}
