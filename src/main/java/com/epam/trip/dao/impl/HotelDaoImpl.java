package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.HotelCsvSource;
import com.epam.trip.entity.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotelDaoImpl implements GenericDao<Hotel> {
    private final HotelCsvSource dataSource;
    private List<Hotel> cache;

    public HotelDaoImpl() {
        this.dataSource = new HotelCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public HotelDaoImpl(HotelCsvSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(h -> h != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Hotel fromCsv(String[] row) {
        if (row.length < 8)
            return null;
        try {
            return new Hotel(
                    Long.parseLong(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    Integer.parseInt(row[4]),
                    Double.parseDouble(row[5]),
                    Integer.parseInt(row[6]),
                    row[7]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Hotel hotel) {
        return new String[] {
                hotel.getId().toString(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getAddress(),
                String.valueOf(hotel.getStarRating()),
                String.valueOf(hotel.getPricePerNight()),
                String.valueOf(hotel.getAvailableRooms()),
                hotel.getAmenities()
        };
    }

    @Override
    public Hotel findById(Long id) {
        return cache.stream()
                .filter(hotel -> hotel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Hotel> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Hotel save(Hotel entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(Hotel::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public Hotel update(Hotel entity) {
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
        cache.removeIf(hotel -> hotel.getId().equals(id));
        saveData();
    }
}
