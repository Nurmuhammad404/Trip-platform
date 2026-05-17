package com.epam.trip.startup;

import com.epam.trip.auth.AuthServiceImpl;
import com.epam.trip.auth.Role;
import com.epam.trip.dao.db.*;
import com.epam.trip.dao.impl.*;
import com.epam.trip.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Seeds the SQLite database with data from CSV files on first run.
 */
public class DataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final DatabaseManager db;

    public DataSeeder(DatabaseManager db) {
        this.db = db;
    }

    public void seedIfEmpty() {
        seedUsers();
        seedHotels();
        seedCars();
        seedPlaces();
        seedTours();
        seedTaxis();
        seedBookings();
    }

    private void seedUsers() {
        JdbcUserDao dao = new JdbcUserDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<User> users = new UserDaoImpl().findAll();
        for (User u : users) {
            // Hash the plain-text passwords from CSV
            u.setPassword(AuthServiceImpl.hash(u.getPassword()));
            if (u.getRole() == null) u.setRole(Role.USER);
            dao.save(u);
        }
        log.info("Seeded {} users from CSV", users.size());
    }

    private void seedHotels() {
        JdbcHotelDao dao = new JdbcHotelDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Hotel> items = new HotelDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} hotels from CSV", items.size());
    }

    private void seedCars() {
        JdbcCarDao dao = new JdbcCarDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Car> items = new CarDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} cars from CSV", items.size());
    }

    private void seedPlaces() {
        JdbcPlaceDao dao = new JdbcPlaceDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Place> items = new PlaceDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} places from CSV", items.size());
    }

    private void seedTours() {
        JdbcTourDao dao = new JdbcTourDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Tour> items = new TourDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} tours from CSV", items.size());
    }

    private void seedTaxis() {
        JdbcTaxiDao dao = new JdbcTaxiDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Taxi> items = new TaxiDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} taxis from CSV", items.size());
    }

    private void seedBookings() {
        JdbcBookingDao dao = new JdbcBookingDao(db);
        if (!dao.findAll().isEmpty()) return;

        List<Booking> items = new BookingDaoImpl().findAll();
        items.forEach(dao::save);
        log.info("Seeded {} bookings from CSV", items.size());
    }
}
