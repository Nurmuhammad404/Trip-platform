package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.TourCsvSource;
import com.epam.trip.entity.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TourDaoImpl implements GenericDao<Tour> {
    private final TourCsvSource dataSource;
    private List<Tour> cache;

    public TourDaoImpl() {
        this.dataSource = new TourCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public TourDaoImpl(TourCsvSource dataSource) {
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

    private Tour fromCsv(String[] row) {
        if (row.length < 9)
            return null;
        try {
            return new Tour(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    Integer.parseInt(row[3]),
                    Double.parseDouble(row[4]),
                    row[5],
                    Integer.parseInt(row[6]),
                    row[7],
                    row[8]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Tour tour) {
        return new String[] {
                tour.getId().toString(),
                tour.getName(),
                tour.getDestination(),
                String.valueOf(tour.getDuration()),
                String.valueOf(tour.getPrice()),
                tour.getDescription(),
                String.valueOf(tour.getMaxGroupSize()),
                tour.getGuide(),
                tour.getSchedule()
        };
    }

    @Override
    public Tour findById(Long id) {
        return cache.stream()
                .filter(tour -> tour.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Tour> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Tour entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream()
                    .mapToLong(Tour::getId)
                    .max()
                    .orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
    }

    @Override
    public void update(Tour entity) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(entity.getId())) {
                cache.set(i, entity);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        cache.removeIf(tour -> tour.getId().equals(id));
        saveData();
    }
}
