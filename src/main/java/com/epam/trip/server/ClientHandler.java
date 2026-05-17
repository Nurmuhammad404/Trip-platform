package com.epam.trip.server;

import com.epam.trip.api.FlightApiService;
import com.epam.trip.auth.AuthService;
import com.epam.trip.auth.Role;
import com.epam.trip.auth.Session;
import com.epam.trip.auth.SessionManager;
import com.epam.trip.dao.db.*;
import com.epam.trip.entity.*;
import com.epam.trip.exception.ServiceException;
import com.epam.trip.exception.ValidationException;
import com.epam.trip.protocol.Command;
import com.epam.trip.protocol.Request;
import com.epam.trip.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Handles one client connection in its own thread.
 * Flights come from Aviationstack API (via FlightApiService); everything else is SQLite.
 */
public class ClientHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private static final Gson GSON = new GsonBuilder().create();

    private final Socket socket;
    private final int clientId;
    private final AuthService authService;
    private final JdbcUserDao userDao;
    private final FlightApiService flightService;
    private final JdbcHotelDao hotelDao;
    private final JdbcCarDao carDao;
    private final JdbcPlaceDao placeDao;
    private final JdbcTourDao tourDao;
    private final JdbcTaxiDao taxiDao;
    private final JdbcBookingDao bookingDao;

    private Session session;

    public ClientHandler(Socket socket, int clientId, AuthService authService,
                         JdbcUserDao userDao, FlightApiService flightService, JdbcHotelDao hotelDao,
                         JdbcCarDao carDao, JdbcPlaceDao placeDao, JdbcTourDao tourDao,
                         JdbcTaxiDao taxiDao, JdbcBookingDao bookingDao) {
        this.socket = socket;
        this.clientId = clientId;
        this.authService = authService;
        this.userDao = userDao;
        this.flightService = flightService;
        this.hotelDao = hotelDao;
        this.carDao = carDao;
        this.placeDao = placeDao;
        this.tourDao = tourDao;
        this.taxiDao = taxiDao;
        this.bookingDao = bookingDao;
        this.session = Session.visitor();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            send(out, Response.ok("Welcome to Trip Platform! Type HELP for commands."));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                Request req;
                try {
                    req = GSON.fromJson(line, Request.class);
                } catch (Exception e) {
                    send(out, Response.error("Invalid JSON request"));
                    continue;
                }
                if (req.getToken() != null) {
                    session = SessionManager.getInstance().get(req.getToken());
                }
                Response resp = dispatch(req);
                send(out, resp);
            }
        } catch (IOException e) {
            log.debug("Client #{} disconnected: {}", clientId, e.getMessage());
        } finally {
            log.info("Client #{} session ended", clientId);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void send(PrintWriter out, Response response) {
        out.println(GSON.toJson(response));
    }

    private Response dispatch(Request req) {
        if (req.getCommand() == null) return Response.error("Missing command");
        try {
            return switch (req.getCommand()) {
                // ── Auth ──────────────────────────────────────
                case LOGIN    -> handleLogin(req);
                case LOGOUT   -> handleLogout(req);
                case REGISTER -> handleRegister(req);

                // ── Browse (no auth required) ─────────────────
                case LIST_FLIGHTS  -> ok(flightService.getAll());
                case LIST_HOTELS   -> ok(hotelDao.findAll());
                case LIST_CARS     -> ok(carDao.findAll());
                case LIST_PLACES   -> ok(placeDao.findAll());
                case LIST_TOURS    -> ok(tourDao.findAll());
                case LIST_TAXIS    -> ok(taxiDao.findAll());
                case SEARCH_FLIGHTS -> handleSearchFlights(req);
                case SEARCH_HOTELS  -> handleSearchHotels(req);
                case SEARCH_CARS    -> handleSearchCars(req);
                case SEARCH_PLACES  -> handleSearchPlaces(req);
                case SEARCH_TAXIS   -> handleSearchTaxis(req);

                // ── User ──────────────────────────────────────
                case BOOK_FLIGHT   -> requireRole(Role.USER, () -> handleBookFlight(req));
                case BOOK_HOTEL    -> requireRole(Role.USER, () -> handleBookHotel(req));
                case RENT_CAR      -> requireRole(Role.USER, () -> handleRentCar(req));
                case BOOK_TAXI     -> requireRole(Role.USER, () -> handleBookTaxi(req));
                case VIEW_BOOKINGS -> requireRole(Role.USER, () -> ok(bookingDao.findByUserId(session.getUserId())));
                case CANCEL_BOOKING -> requireRole(Role.USER, () -> handleCancelBooking(req));
                case ADD_HOTEL     -> requireRole(Role.USER, () -> handleAddHotel(req));
                case ADD_CAR       -> requireRole(Role.USER, () -> handleAddCar(req));
                case ADD_PLACE     -> requireRole(Role.USER, () -> handleAddPlace(req));
                case ADD_TOUR      -> requireRole(Role.USER, () -> handleAddTour(req));
                case ADD_TAXI      -> requireRole(Role.USER, () -> handleAddTaxi(req));
                case DELETE_HOTEL  -> requireRole(Role.USER, () -> handleDelete(hotelDao, req));
                case DELETE_CAR    -> requireRole(Role.USER, () -> handleDelete(carDao, req));
                case DELETE_PLACE  -> requireRole(Role.USER, () -> handleDelete(placeDao, req));
                case DELETE_TOUR   -> requireRole(Role.USER, () -> handleDelete(tourDao, req));
                case DELETE_TAXI   -> requireRole(Role.USER, () -> handleDelete(taxiDao, req));

                // ── Admin — flights managed via live API ──────
                case ADD_FLIGHT    -> requireRole(Role.ADMIN, () -> handleAddFlight(req));
                case DELETE_FLIGHT -> requireRole(Role.ADMIN, () -> handleDeleteFlight(req));

                // ── Admin — users ─────────────────────────────
                case LIST_USERS       -> requireRole(Role.ADMIN, () -> ok(userDao.findAll()));
                case ADD_USER         -> requireRole(Role.ADMIN, () -> handleAdminAddUser(req));
                case DELETE_USER      -> requireRole(Role.ADMIN, () -> handleAdminDeleteUser(req));
                case UPDATE_USER_ROLE -> requireRole(Role.ADMIN, () -> handleUpdateUserRole(req));

                // ── Meta ──────────────────────────────────────
                case PING -> Response.ok("PONG");
                case HELP -> Response.ok(helpText());
            };
        } catch (ValidationException e) {
            return Response.error("Validation: " + e.getMessage());
        } catch (ServiceException e) {
            return Response.error(e.getMessage());
        } catch (Exception e) {
            log.error("Unhandled error for command {}", req.getCommand(), e);
            return Response.error("Server error: " + e.getMessage());
        }
    }

    // ── Auth handlers ────────────────────────────────────────────────────────

    private Response handleLogin(Request req) {
        String username = req.getParam("username");
        String password = req.getParam("password");
        session = authService.login(username, password);
        return Response.ok("Logged in as " + session.getUsername() + " [" + session.getRole() + "]",
                Map.of("token", session.getToken(), "role", session.getRole().name()));
    }

    private Response handleLogout(Request req) {
        authService.logout(req.getToken());
        session = Session.visitor();
        return Response.ok("Logged out");
    }

    private Response handleRegister(Request req) {
        session = authService.register(
                req.getParam("username"),
                req.getParam("email"),
                req.getParam("password"),
                req.getParam("fullName", ""),
                req.getParam("phone", ""));
        return Response.ok("Registered and logged in as " + session.getUsername(),
                Map.of("token", session.getToken(), "role", session.getRole().name()));
    }

    // ── Search handlers ──────────────────────────────────────────────────────

    private Response handleSearchFlights(Request req) {
        String dep  = req.getParam("departure", "");
        String dest = req.getParam("destination", "");
        return ok(flightService.search(dep, dest));
    }

    private Response handleSearchHotels(Request req) {
        return ok(hotelDao.findByCity(req.getParam("city", "")));
    }

    private Response handleSearchCars(Request req) {
        return ok(carDao.findByLocation(req.getParam("location", "")));
    }

    private Response handleSearchPlaces(Request req) {
        String city = req.getParam("city");
        String cat  = req.getParam("category");
        if (city != null) return ok(placeDao.findByCity(city));
        if (cat  != null) return ok(placeDao.findByCategory(cat));
        return ok(placeDao.findAll());
    }

    private Response handleSearchTaxis(Request req) {
        return ok(taxiDao.findByCity(req.getParam("city", "")));
    }

    // ── Booking handlers ─────────────────────────────────────────────────────

    private Response handleBookFlight(Request req) {
        long id = parseLong(req, "id");
        Flight f = flightService.findById(id);
        if (f == null) return Response.error("Flight not found — try listing flights first");
        Booking b = booking("FLIGHT", id, f.getPrice(), req);
        bookingDao.save(b);
        return Response.ok("Flight booked! Booking id: " + b.getId(), b);
    }

    private Response handleBookHotel(Request req) {
        long id = parseLong(req, "id");
        Hotel h = hotelDao.findById(id);
        if (h == null) return Response.error("Hotel not found");
        if (h.getAvailableRooms() <= 0) return Response.error("No rooms available");
        h.setAvailableRooms(h.getAvailableRooms() - 1);
        hotelDao.update(h);
        Booking b = booking("HOTEL", id, h.getPricePerNight(), req);
        bookingDao.save(b);
        return Response.ok("Hotel booked! Booking id: " + b.getId(), b);
    }

    private Response handleRentCar(Request req) {
        long id = parseLong(req, "id");
        Car c = carDao.findById(id);
        if (c == null) return Response.error("Car not found");
        if (!c.isAvailable()) return Response.error("Car not available");
        c.setAvailable(false);
        carDao.update(c);
        Booking b = booking("CAR", id, c.getPricePerDay(), req);
        bookingDao.save(b);
        return Response.ok("Car rented! Booking id: " + b.getId(), b);
    }

    private Response handleBookTaxi(Request req) {
        long id = parseLong(req, "id");
        Taxi t = taxiDao.findById(id);
        if (t == null) return Response.error("Taxi not found");
        if (!t.isAvailable()) return Response.error("Taxi not available");
        t.setAvailable(false);
        taxiDao.update(t);
        Booking b = booking("TAXI", id, t.getPricePerKm(), req);
        bookingDao.save(b);
        return Response.ok("Taxi booked! Booking id: " + b.getId(), b);
    }

    private Response handleCancelBooking(Request req) {
        long id = parseLong(req, "id");
        Booking b = bookingDao.findById(id);
        if (b == null) return Response.error("Booking not found");
        if (!b.getUserId().equals(session.getUserId()) && session.getRole() != Role.ADMIN)
            return Response.forbidden("Not your booking");
        b.setStatus("CANCELLED");
        bookingDao.update(b);
        return Response.ok("Booking " + id + " cancelled");
    }

    // ── Flight admin handlers (in-memory) ────────────────────────────────────

    private Response handleAddFlight(Request req) {
        Flight f = new Flight();
        f.setFlightNumber(req.getParam("flightNumber", "XX000"));
        f.setDeparture(req.getParam("departure", ""));
        f.setDestination(req.getParam("destination", ""));
        f.setDepartureTime(req.getParam("departureTime", "00:00"));
        f.setArrivalTime(req.getParam("arrivalTime", "00:00"));
        f.setPrice(parseDouble(req, "price", 0.0));
        f.setAvailableSeats(parseInt(req, "seats", 100));
        f.setAirline(req.getParam("airline", ""));
        flightService.add(f);
        return Response.ok("Flight added (id: " + f.getId() + ")", f);
    }

    private Response handleDeleteFlight(Request req) {
        long id = parseLong(req, "id");
        if (!flightService.remove(id)) return Response.error("Flight not found in cache");
        return Response.ok("Flight " + id + " removed");
    }

    // ── Hotel / Car / Place / Tour / Taxi add handlers ───────────────────────

    private Response handleAddHotel(Request req) {
        Hotel h = new Hotel();
        h.setName(req.getParam("name", ""));
        h.setCity(req.getParam("city", ""));
        h.setAddress(req.getParam("address", ""));
        h.setStarRating(parseInt(req, "stars", 3));
        h.setPricePerNight(parseDouble(req, "price", 0.0));
        h.setAvailableRooms(parseInt(req, "rooms", 10));
        h.setAmenities(req.getParam("amenities", ""));
        hotelDao.save(h);
        return Response.ok("Hotel added with id " + h.getId(), h);
    }

    private Response handleAddCar(Request req) {
        Car c = new Car();
        c.setBrand(req.getParam("brand", ""));
        c.setModel(req.getParam("model", ""));
        c.setType(req.getParam("type", "Sedan"));
        c.setPricePerDay(parseDouble(req, "price", 0.0));
        c.setAvailable(true);
        c.setLocation(req.getParam("location", ""));
        c.setSeats(parseInt(req, "seats", 5));
        c.setTransmission(req.getParam("transmission", "Automatic"));
        carDao.save(c);
        return Response.ok("Car added with id " + c.getId(), c);
    }

    private Response handleAddPlace(Request req) {
        Place p = new Place();
        p.setName(req.getParam("name", ""));
        p.setCity(req.getParam("city", ""));
        p.setCountry(req.getParam("country", ""));
        p.setDescription(req.getParam("description", ""));
        p.setCategory(req.getParam("category", "Landmark"));
        p.setRating(parseDouble(req, "rating", 0.0));
        p.setEntryFee(parseDouble(req, "entryFee", 0.0));
        placeDao.save(p);
        return Response.ok("Place added with id " + p.getId(), p);
    }

    private Response handleAddTour(Request req) {
        Tour t = new Tour();
        t.setName(req.getParam("name", ""));
        t.setDestination(req.getParam("destination", ""));
        t.setDuration(parseInt(req, "duration", 1));
        t.setPrice(parseDouble(req, "price", 0.0));
        t.setDescription(req.getParam("description", ""));
        t.setMaxGroupSize(parseInt(req, "groupSize", 10));
        t.setGuide(req.getParam("guide", ""));
        t.setSchedule(req.getParam("schedule", ""));
        tourDao.save(t);
        return Response.ok("Tour added with id " + t.getId(), t);
    }

    private Response handleAddTaxi(Request req) {
        Taxi t = new Taxi();
        t.setDriverName(req.getParam("driverName", ""));
        t.setVehicleType(req.getParam("vehicleType", "Sedan"));
        t.setLicensePlate(req.getParam("licensePlate", ""));
        t.setCity(req.getParam("city", ""));
        t.setPricePerKm(parseDouble(req, "pricePerKm", 0.0));
        t.setAvailable(true);
        t.setPhoneNumber(req.getParam("phone", ""));
        t.setRating(parseDouble(req, "rating", 0.0));
        taxiDao.save(t);
        return Response.ok("Taxi added with id " + t.getId(), t);
    }

    private Response handleDelete(BaseJdbcDao<?> dao, Request req) {
        long id = parseLong(req, "id");
        if (dao.findById(id) == null) return Response.error("Item not found");
        dao.delete(id);
        return Response.ok("Deleted item with id " + id);
    }

    // ── Admin — user management ──────────────────────────────────────────────

    private Response handleAdminAddUser(Request req) {
        session = authService.register(
                req.getParam("username"),
                req.getParam("email"),
                req.getParam("password"),
                req.getParam("fullName", ""),
                req.getParam("phone", ""));
        return Response.ok("User created", Map.of("id", session.getUserId()));
    }

    private Response handleAdminDeleteUser(Request req) {
        long id = parseLong(req, "id");
        if (userDao.findById(id) == null) return Response.error("User not found");
        userDao.delete(id);
        return Response.ok("User " + id + " deleted");
    }

    private Response handleUpdateUserRole(Request req) {
        long id = parseLong(req, "id");
        User u = userDao.findById(id);
        if (u == null) return Response.error("User not found");
        try {
            u.setRole(Role.valueOf(req.getParam("role", "USER").toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Response.error("Invalid role. Use: VISITOR, USER, ADMIN");
        }
        userDao.update(u);
        return Response.ok("Role updated to " + u.getRole());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Response requireRole(Role required, java.util.concurrent.Callable<Response> action) {
        if (!session.hasRole(required)) {
            return session.isAuthenticated()
                    ? Response.forbidden("Requires " + required + " role")
                    : Response.unauthorized("Please login first");
        }
        try {
            return action.call();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private Response ok(List<?> items) {
        return Response.ok(items.size() + " item(s) found", items);
    }

    private Booking booking(String type, long serviceId, double price, Request req) {
        Booking b = new Booking();
        b.setUserId(session.getUserId());
        b.setServiceType(type);
        b.setServiceId(serviceId);
        b.setBookingDate(LocalDate.now().toString());
        b.setStatus("CONFIRMED");
        b.setTotalPrice(price);
        b.setCustomerName(session.getUsername());
        User user = userDao.findById(session.getUserId());
        b.setCustomerEmail(user != null ? user.getEmail() : "");
        return b;
    }

    private long parseLong(Request req, String key) {
        try { return Long.parseLong(req.getParam(key, "0")); }
        catch (NumberFormatException e) { throw new ValidationException("Invalid id"); }
    }

    private int parseInt(Request req, String key, int def) {
        try { return Integer.parseInt(req.getParam(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }

    private double parseDouble(Request req, String key, double def) {
        try { return Double.parseDouble(req.getParam(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }

    private String helpText() {
        return """
                Available commands:
                  AUTH:    LOGIN, LOGOUT, REGISTER
                  BROWSE:  LIST_FLIGHTS (live API), LIST_HOTELS, LIST_CARS, LIST_PLACES, LIST_TOURS, LIST_TAXIS
                  SEARCH:  SEARCH_FLIGHTS, SEARCH_HOTELS, SEARCH_CARS, SEARCH_PLACES, SEARCH_TAXIS
                  BOOK:    BOOK_FLIGHT, BOOK_HOTEL, RENT_CAR, BOOK_TAXI  [requires login]
                  MANAGE:  ADD_HOTEL, ADD_CAR, ADD_PLACE, ADD_TOUR, ADD_TAXI  [requires login]
                           DELETE_HOTEL, DELETE_CAR, DELETE_PLACE, DELETE_TOUR, DELETE_TAXI
                  BOOKINGS: VIEW_BOOKINGS, CANCEL_BOOKING  [requires login]
                  ADMIN:   ADD_FLIGHT, DELETE_FLIGHT (in-memory cache)
                           LIST_USERS, ADD_USER, DELETE_USER, UPDATE_USER_ROLE  [requires ADMIN]
                  META:    PING, HELP""";
    }
}
