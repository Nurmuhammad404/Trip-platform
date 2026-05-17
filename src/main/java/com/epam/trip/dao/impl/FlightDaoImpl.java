package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.DataSource;
import com.epam.trip.dao.source.csv.FlightCsvSource;
import com.epam.trip.entity.Flight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDaoImpl implements GenericDao<Flight> {
    private final DataSource dataSource;
    private List<Flight> cache;

    public FlightDaoImpl() {
        this.dataSource = new FlightCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public FlightDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(f -> f != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Flight fromCsv(String[] row) {
        if (row.length < 9)
            return null;
        try {
            return new Flight(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    Double.parseDouble(row[6]),
                    Integer.parseInt(row[7]),
                    row[8]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Flight flight) {
        return new String[] {
                flight.getId().toString(),
                flight.getFlightNumber(),
                flight.getDeparture(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                String.valueOf(flight.getPrice()),
                String.valueOf(flight.getAvailableSeats()),
                flight.getAirline()
        };
    }

    @Override
    public Flight findById(Long id) {
        return cache.stream()
                .filter(flight -> flight.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Flight> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Flight save(Flight entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(Flight::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public Flight update(Flight entity) {
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
        cache.removeIf(flight -> flight.getId().equals(id));
        saveData();
    }
}
