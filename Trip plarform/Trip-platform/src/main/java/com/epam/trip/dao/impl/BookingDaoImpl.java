package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.BookingCsvSource;
import com.epam.trip.entity.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingDaoImpl implements GenericDao<Booking> {
    private final BookingCsvSource dataSource;
    private List<Booking> cache;

    public BookingDaoImpl() {
        this.dataSource = new BookingCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public BookingDaoImpl(BookingCsvSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .filter(b -> b != null)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private Booking fromCsv(String[] row) {
        if (row.length < 9)
            return null;
        try {
            return new Booking(
                    Long.parseLong(row[0]),
                    Long.parseLong(row[1]),
                    row[2],
                    Long.parseLong(row[3]),
                    row[4],
                    row[5],
                    Double.parseDouble(row[6]),
                    row[7],
                    row[8]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] toCsv(Booking booking) {
        return new String[] {
                booking.getId().toString(),
                booking.getUserId().toString(),
                booking.getServiceType(),
                booking.getServiceId().toString(),
                booking.getBookingDate(),
                booking.getStatus(),
                String.valueOf(booking.getTotalPrice()),
                booking.getCustomerName(),
                booking.getCustomerEmail()
        };
    }

    @Override
    public Booking findById(Long id) {
        return cache.stream()
                .filter(booking -> booking.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Booking entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream()
                    .mapToLong(Booking::getId)
                    .max()
                    .orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
    }

    @Override
    public void update(Booking entity) {
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
        cache.removeIf(booking -> booking.getId().equals(id));
        saveData();
    }
}
