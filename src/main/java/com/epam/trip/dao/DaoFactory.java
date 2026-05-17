package com.epam.trip.dao;

import com.epam.trip.dao.impl.*;
import com.epam.trip.dao.source.api.FlightApiSource;
import com.epam.trip.dao.source.api.PlaceApiSource;
import com.epam.trip.dao.source.csv.*;
import com.epam.trip.entity.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DaoFactory {
    private static UserDaoImpl userDao;
    private static FlightDaoImpl flightDao;
    private static HotelDaoImpl hotelDao;
    private static CarDaoImpl carDao;
    private static PlaceDaoImpl placeDao;
    private static TourDaoImpl tourDao;
    private static TaxiDaoImpl taxiDao;
    private static BookingDaoImpl bookingDao;

    private static Properties config;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        config = new Properties();
        try (InputStream input = DaoFactory.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (input != null) {
                config.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    private static boolean isApiEnabled(String service) {
        boolean globalEnabled = Boolean.parseBoolean(config.getProperty("api.enabled", "false"));
        boolean serviceEnabled = Boolean.parseBoolean(config.getProperty("api." + service + ".enabled", "false"));
        return globalEnabled && serviceEnabled;
    }

    private static String getApiKey(String service) {
        return config.getProperty("api." + service + ".key", "");
    }

    private static String getApiUrl(String service) {
        return config.getProperty("api." + service + ".url", "");
    }

    private static int getTimeout() {
        return Integer.parseInt(config.getProperty("api.timeout.seconds", "30"));
    }

    private static int getRetryCount() {
        return Integer.parseInt(config.getProperty("api.retry.count", "2"));
    }

    public static GenericDao<User> getUserDao() {
        if (userDao == null) {
            userDao = new UserDaoImpl();
        }
        return userDao;
    }

    public static GenericDao<Flight> getFlightDao() {
        if (flightDao == null) {
            if (isApiEnabled("aviationstack")) {
                FlightApiSource apiSource = new FlightApiSource(
                        true,
                        new FlightCsvSource(),
                        getApiKey("aviationstack"),
                        getApiUrl("aviationstack"),
                        getTimeout(),
                        getRetryCount());
                flightDao = new FlightDaoImpl(apiSource);
            } else {
                flightDao = new FlightDaoImpl();
            }
        }
        return flightDao;
    }

    public static GenericDao<Hotel> getHotelDao() {
        if (hotelDao == null) {
            hotelDao = new HotelDaoImpl();
        }
        return hotelDao;
    }

    public static GenericDao<Car> getCarDao() {
        if (carDao == null) {
            carDao = new CarDaoImpl();
        }
        return carDao;
    }

    public static GenericDao<Place> getPlaceDao() {
        if (placeDao == null) {
            if (isApiEnabled("openrouteservice")) {
                PlaceApiSource apiSource = new PlaceApiSource(
                        true,
                        new PlaceCsvSource(),
                        getApiKey("openrouteservice"),
                        getApiUrl("openrouteservice"),
                        getTimeout(),
                        getRetryCount());
                placeDao = new PlaceDaoImpl(apiSource);
            } else {
                placeDao = new PlaceDaoImpl();
            }
        }
        return placeDao;
    }

    public static GenericDao<Tour> getTourDao() {
        if (tourDao == null) {
            tourDao = new TourDaoImpl();
        }
        return tourDao;
    }

    public static GenericDao<Taxi> getTaxiDao() {
        if (taxiDao == null) {
            taxiDao = new TaxiDaoImpl();
        }
        return taxiDao;
    }

    public static GenericDao<Booking> getBookingDao() {
        if (bookingDao == null) {
            bookingDao = new BookingDaoImpl();
        }
        return bookingDao;
    }
}
