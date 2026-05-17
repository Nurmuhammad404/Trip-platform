package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.DataSource;
import com.epam.trip.dao.source.csv.PlaceCsvSource;
import com.epam.trip.entity.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceDaoImpl implements GenericDao<Place> {
    private final DataSource dataSource;
    private List<Place> cache;

    public PlaceDaoImpl() {
        this.dataSource = new PlaceCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public PlaceDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Place fromCsv(String[] row) {
        if (row.length < 8)
            return null;
        try {
            return new Place(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    Double.parseDouble(row[6]),
                    Double.parseDouble(row[7]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Place place) {
        return new String[] {
                place.getId().toString(),
                place.getName(),
                place.getCity(),
                place.getCountry(),
                place.getDescription(),
                place.getCategory(),
                String.valueOf(place.getRating()),
                String.valueOf(place.getEntryFee())
        };
    }

    @Override
    public Place findById(Long id) {
        return cache.stream()
                .filter(place -> place.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Place> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Place save(Place entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(Place::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public Place update(Place entity) {
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
        cache.removeIf(place -> place.getId().equals(id));
        saveData();
    }
}
