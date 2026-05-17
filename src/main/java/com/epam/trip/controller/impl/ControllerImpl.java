package com.epam.trip.controller.impl;

import com.epam.trip.controller.Controller;
import com.epam.trip.entity.*;
import com.epam.trip.service.*;
import com.epam.trip.service.impl.*;
import com.epam.trip.view.View;

import java.util.List;

public class ControllerImpl implements Controller {
    private final UserServiceImpl userService;
    private final FlightServiceImpl flightService;
    private final HotelServiceImpl hotelService;
    private final CarServiceImpl carService;
    private final PlaceServiceImpl placeService;
    private final TourServiceImpl tourService;
    private final TaxiServiceImpl taxiService;
    private final BookingServiceImpl bookingService;
    private final View view;

    private User currentUser;

    public ControllerImpl(View view) {
        this.view = view;
        this.userService = (UserServiceImpl) ServiceFactory.getUserService();
        this.flightService = (FlightServiceImpl) ServiceFactory.getFlightService();
        this.hotelService = (HotelServiceImpl) ServiceFactory.getHotelService();
        this.carService = (CarServiceImpl) ServiceFactory.getCarService();
        this.placeService = (PlaceServiceImpl) ServiceFactory.getPlaceService();
        this.tourService = (TourServiceImpl) ServiceFactory.getTourService();
        this.taxiService = (TaxiServiceImpl) ServiceFactory.getTaxiService();
        this.bookingService = (BookingServiceImpl) ServiceFactory.getBookingService();
    }

    @Override
    public void processRequest(String request) {
        if (request == null || request.trim().isEmpty()) {
            view.displayMessage("Invalid command");
            return;
        }

        String command = request.trim().toUpperCase();

        switch (command) {
            case "1":
            case "FLIGHTS":
                handleFlights();
                break;
            case "2":
            case "HOTELS":
                handleHotels();
                break;
            case "3":
            case "CARS":
                handleCars();
                break;
            case "4":
            case "PLACES":
                handlePlaces();
                break;
            case "5":
            case "TOURS":
                handleTours();
                break;
            case "6":
            case "TAXIS":
                handleTaxis();
                break;
            case "7":
            case "BOOKINGS":
                handleBookings();
                break;
            case "8":
            case "LOGIN":
                handleLogin();
                break;
            case "9":
            case "EXIT":
                handleExit();
                break;
            default:
                view.displayMessage("Unknown command: " + command);
                break;
        }
    }

    private void handleFlights() {
        view.displayMessage("\n=== Flight Management ===");
        view.displayMessage("1. View all flights");
        view.displayMessage("2. Search flights by route");
        view.displayMessage("3. Book a flight");
        view.displayMessage("Choose option: ");

        String choice = view.getUserInput();

        switch (choice) {
            case "1":
                List<Flight> flights = flightService.findAll();
                displayFlights(flights);
                break;
            case "2":
                view.displayMessage("Enter departure city: ");
                String departure = view.getUserInput();
                view.displayMessage("Enter destination city: ");
                String destination = view.getUserInput();
                List<Flight> searchResults = flightService.searchByRoute(departure, destination);
                displayFlights(searchResults);
                break;
            case "3":
                bookFlight();
                break;
        }
    }

    private void handleHotels() {
        view.displayMessage("\n=== Hotel Management ===");
        view.displayMessage("1. View all hotels");
        view.displayMessage("2. Search hotels by city");
        view.displayMessage("3. Book a hotel");
        view.displayMessage("Choose option: ");

        String choice = view.getUserInput();

        switch (choice) {
            case "1":
                List<Hotel> hotels = hotelService.findAll();
                displayHotels(hotels);
                break;
            case "2":
                view.displayMessage("Enter city: ");
                String city = view.getUserInput();
                List<Hotel> searchResults = hotelService.searchByCity(city);
                displayHotels(searchResults);
                break;
            case "3":
                bookHotel();
                break;
        }
    }

    private void handleCars() {
        view.displayMessage("\n=== Car Rental ===");
        view.displayMessage("1. View available cars");
        view.displayMessage("2. Search by location");
        view.displayMessage("3. Rent a car");
        view.displayMessage("Choose option: ");

        String choice = view.getUserInput();

        switch (choice) {
            case "1":
                List<Car> cars = carService.getAvailableCars();
                displayCars(cars);
                break;
            case "2":
                view.displayMessage("Enter location: ");
                String location = view.getUserInput();
                List<Car> searchResults = carService.searchByLocation(location);
                displayCars(searchResults);
                break;
            case "3":
                rentCar();
                break;
        }
    }

    private void handlePlaces() {
        view.displayMessage("\n=== Places to Visit ===");
        List<Place> places = placeService.findAll();
        displayPlaces(places);
    }

    private void handleTours() {
        view.displayMessage("\n=== Group Tours ===");
        List<Tour> tours = tourService.findAll();
        displayTours(tours);
    }

    private void handleTaxis() {
        view.displayMessage("\n=== Taxi Service ===");
        view.displayMessage("1. View available taxis");
        view.displayMessage("2. Search by city");
        view.displayMessage("3. Book a taxi");
        view.displayMessage("Choose option: ");

        String choice = view.getUserInput();

        switch (choice) {
            case "1":
                List<Taxi> taxis = taxiService.getAvailableTaxis();
                displayTaxis(taxis);
                break;
            case "2":
                view.displayMessage("Enter city: ");
                String city = view.getUserInput();
                List<Taxi> searchResults = taxiService.searchByCity(city);
                displayTaxis(searchResults);
                break;
            case "3":
                bookTaxi();
                break;
        }
    }

    private void handleBookings() {
        view.displayMessage("\n=== My Bookings ===");
        if (currentUser == null) {
            view.displayMessage("Please login first");
            return;
        }
        List<Booking> bookings = bookingService.findByUserId(currentUser.getId());
        displayBookings(bookings);
    }

    private void handleLogin() {
        view.displayMessage("\n=== Login ===");
        view.displayMessage("Enter username: ");
        String username = view.getUserInput();
        view.displayMessage("Enter password: ");
        String password = view.getUserInput();

        if (userService.authenticate(username, password)) {
            currentUser = userService.findByUsername(username);
            view.displayMessage("Login successful! Welcome, " + currentUser.getFullName());
        } else {
            view.displayMessage("Invalid credentials");
        }
    }

    private void handleExit() {
        view.displayMessage("Thank you for using Trip Platform. Goodbye!");
        System.exit(0);
    }

    private void bookFlight() {
        view.displayMessage("Enter flight ID: ");
        Long flightId = Long.parseLong(view.getUserInput());
        if (flightService.bookSeat(flightId)) {
            view.displayMessage("Flight booked successfully!");
        } else {
            view.displayMessage("Booking failed. Flight not available.");
        }
    }

    private void bookHotel() {
        view.displayMessage("Enter hotel ID: ");
        Long hotelId = Long.parseLong(view.getUserInput());
        if (hotelService.bookRoom(hotelId)) {
            view.displayMessage("Hotel booked successfully!");
        } else {
            view.displayMessage("Booking failed. No rooms available.");
        }
    }

    private void rentCar() {
        view.displayMessage("Enter car ID: ");
        Long carId = Long.parseLong(view.getUserInput());
        if (carService.rentCar(carId)) {
            view.displayMessage("Car rented successfully!");
        } else {
            view.displayMessage("Rental failed. Car not available.");
        }
    }

    private void bookTaxi() {
        view.displayMessage("Enter taxi ID: ");
        Long taxiId = Long.parseLong(view.getUserInput());
        if (taxiService.bookTaxi(taxiId)) {
            view.displayMessage("Taxi booked successfully!");
        } else {
            view.displayMessage("Booking failed. Taxi not available.");
        }
    }

    private void displayFlights(List<Flight> flights) {
        if (flights.isEmpty()) {
            view.displayMessage("No flights found.");
            return;
        }
        for (Flight flight : flights) {
            view.displayMessage(String.format("ID: %d | %s: %s -> %s | Departs: %s | Price: $%.2f | Seats: %d",
                    flight.getId(), flight.getFlightNumber(), flight.getDeparture(),
                    flight.getDestination(), flight.getDepartureTime(),
                    flight.getPrice(), flight.getAvailableSeats()));
        }
    }

    private void displayHotels(List<Hotel> hotels) {
        if (hotels.isEmpty()) {
            view.displayMessage("No hotels found.");
            return;
        }
        for (Hotel hotel : hotels) {
            view.displayMessage(String.format("ID: %d | %s (%d★) | %s | $%.2f/night | Rooms: %d",
                    hotel.getId(), hotel.getName(), hotel.getStarRating(),
                    hotel.getCity(), hotel.getPricePerNight(), hotel.getAvailableRooms()));
        }
    }

    private void displayCars(List<Car> cars) {
        if (cars.isEmpty()) {
            view.displayMessage("No cars found.");
            return;
        }
        for (Car car : cars) {
            view.displayMessage(String.format("ID: %d | %s %s (%s) | %s | $%.2f/day",
                    car.getId(), car.getBrand(), car.getModel(), car.getType(),
                    car.getLocation(), car.getPricePerDay()));
        }
    }

    private void displayPlaces(List<Place> places) {
        if (places.isEmpty()) {
            view.displayMessage("No places found.");
            return;
        }
        for (Place place : places) {
            view.displayMessage(String.format("ID: %d | %s | %s, %s | Rating: %.1f | Entry: $%.2f",
                    place.getId(), place.getName(), place.getCity(),
                    place.getCountry(), place.getRating(), place.getEntryFee()));
        }
    }

    private void displayTours(List<Tour> tours) {
        if (tours.isEmpty()) {
            view.displayMessage("No tours found.");
            return;
        }
        for (Tour tour : tours) {
            view.displayMessage(String.format("ID: %d | %s | %s | %d days | $%.2f",
                    tour.getId(), tour.getName(), tour.getDestination(),
                    tour.getDuration(), tour.getPrice()));
        }
    }

    private void displayTaxis(List<Taxi> taxis) {
        if (taxis.isEmpty()) {
            view.displayMessage("No taxis found.");
            return;
        }
        for (Taxi taxi : taxis) {
            view.displayMessage(String.format("ID: %d | Driver: %s | %s | %s | Rating: %.1f",
                    taxi.getId(), taxi.getDriverName(), taxi.getVehicleType(),
                    taxi.getCity(), taxi.getRating()));
        }
    }

    private void displayBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            view.displayMessage("No bookings found.");
            return;
        }
        for (Booking booking : bookings) {
            view.displayMessage(String.format("ID: %d | %s #%d | Date: %s | Status: %s | $%.2f",
                    booking.getId(), booking.getServiceType(), booking.getServiceId(),
                    booking.getBookingDate(), booking.getStatus(), booking.getTotalPrice()));
        }
    }
}