package com.epam.trip.service;

import com.epam.trip.service.impl.*;

public class ServiceFactory {
    private static UserServiceImpl userService;
    private static FlightServiceImpl flightService;
    private static HotelServiceImpl hotelService;
    private static CarServiceImpl carService;
    private static PlaceServiceImpl placeService;
    private static TourServiceImpl tourService;
    private static TaxiServiceImpl taxiService;
    private static BookingServiceImpl bookingService;

    public static UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }

    public static FlightService getFlightService() {
        if (flightService == null) {
            flightService = new FlightServiceImpl();
        }
        return flightService;
    }

    public static HotelService getHotelService() {
        if (hotelService == null) {
            hotelService = new HotelServiceImpl();
        }
        return hotelService;
    }

    public static CarService getCarService() {
        if (carService == null) {
            carService = new CarServiceImpl();
        }
        return carService;
    }

    public static PlaceService getPlaceService() {
        if (placeService == null) {
            placeService = new PlaceServiceImpl();
        }
        return placeService;
    }

    public static TourService getTourService() {
        if (tourService == null) {
            tourService = new TourServiceImpl();
        }
        return tourService;
    }

    public static TaxiService getTaxiService() {
        if (taxiService == null) {
            taxiService = new TaxiServiceImpl();
        }
        return taxiService;
    }

    public static BookingService getBookingService() {
        if (bookingService == null) {
            bookingService = new BookingServiceImpl();
        }
        return bookingService;
    }
}