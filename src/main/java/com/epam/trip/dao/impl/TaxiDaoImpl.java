package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.TaxiCsvSource;
import com.epam.trip.entity.Taxi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaxiDaoImpl implements GenericDao<Taxi> {
    private final TaxiCsvSource dataSource;
    private List<Taxi> cache;

    public TaxiDaoImpl() {
        this.dataSource = new TaxiCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public TaxiDaoImpl(TaxiCsvSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Taxi fromCsv(String[] row) {
        if (row.length < 9)
            return null;
        try {
            return new Taxi(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    Double.parseDouble(row[5]),
                    Boolean.parseBoolean(row[6]),
                    row[7],
                    Double.parseDouble(row[8]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Taxi taxi) {
        return new String[] {
                taxi.getId().toString(),
                taxi.getDriverName(),
                taxi.getVehicleType(),
                taxi.getLicensePlate(),
                taxi.getCity(),
                String.valueOf(taxi.getPricePerKm()),
                String.valueOf(taxi.isAvailable()),
                taxi.getPhoneNumber(),
                String.valueOf(taxi.getRating())
        };
    }

    @Override
    public Taxi findById(Long id) {
        return cache.stream()
                .filter(taxi -> taxi.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Taxi> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Taxi save(Taxi entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(Taxi::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public Taxi update(Taxi entity) {
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
        cache.removeIf(taxi -> taxi.getId().equals(id));
        saveData();
    }
}
