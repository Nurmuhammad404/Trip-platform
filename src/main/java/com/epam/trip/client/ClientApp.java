package com.epam.trip.client;

import com.epam.trip.protocol.Command;
import com.epam.trip.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientApp {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 7777;

    private ServerConnection conn;
    private String currentRole = "VISITOR";
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int    port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        new ClientApp().run(host, port);
    }

    private void run(String host, int port) {
        try {
            conn = new ServerConnection(host, port);
            printWelcome();
            showMenu();

            while (true) {
                System.out.print("\n[" + currentRole + "] Enter choice: ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) { showMenu(); continue; }
                if (input.equalsIgnoreCase("EXIT") || input.equalsIgnoreCase("0")) {
                    System.out.println("\nGoodbye! Safe travels!");
                    break;
                }
                if (input.equalsIgnoreCase("MENU") || input.equalsIgnoreCase("M")) {
                    showMenu(); continue;
                }
                processInput(input);
                System.out.println("\nPress Enter to return to menu...");
                scanner.nextLine();
                showMenu();
            }
        } catch (IOException e) {
            System.err.println("Cannot connect to server at " + host + ":" + port);
            System.err.println("Make sure the server is running first.");
        } finally {
            if (conn != null) try { conn.close(); } catch (IOException ignored) {}
        }
    }

    // ── Menus ────────────────────────────────────────────────────────────────

    private void printWelcome() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         TRIP PLATFORM  v2.0                  ║");
        System.out.println("║    Your Complete Travel Management Solution   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }

    private void showMenu() {
        System.out.println();
        System.out.println("══════════════════════════════════════════════");
        switch (currentRole) {
            case "VISITOR" -> showVisitorMenu();
            case "USER"    -> showUserMenu();
            case "ADMIN"   -> showAdminMenu();
        }
        System.out.println("══════════════════════════════════════════════");
    }

    private void showVisitorMenu() {
        System.out.println("  WELCOME — Please login or register to book");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  [1] Login");
        System.out.println("  [2] Register as new customer");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  [3] Browse Flights");
        System.out.println("  [4] Browse Hotels");
        System.out.println("  [5] Browse Cars");
        System.out.println("  [6] Browse Places");
        System.out.println("  [7] Browse Tours");
        System.out.println("  [8] Browse Taxis");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  [0] Exit");
    }

    private void showUserMenu() {
        System.out.println("  CUSTOMER MENU");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  BROWSE");
        System.out.println("  [1] Flights        [2] Hotels");
        System.out.println("  [3] Cars            [4] Places");
        System.out.println("  [5] Tours           [6] Taxis");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  SEARCH");
        System.out.println("  [7] Search Flights  [8] Search Hotels");
        System.out.println("  [9] Search Cars     [10] Search Taxis");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  BOOK");
        System.out.println("  [11] Book Flight    [12] Book Hotel");
        System.out.println("  [13] Rent Car       [14] Book Taxi");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  MY ACCOUNT");
        System.out.println("  [15] My Bookings    [16] Cancel Booking");
        System.out.println("  [17] Logout");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  [0] Exit");
    }

    private void showAdminMenu() {
        System.out.println("  ADMIN PANEL");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  MANAGE DATA");
        System.out.println("  [1] List Flights    [2] List Hotels");
        System.out.println("  [3] List Cars       [4] List Places");
        System.out.println("  [5] List Tours      [6] List Taxis");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  ADD");
        System.out.println("  [7] Add Flight      [8] Add Hotel");
        System.out.println("  [9] Add Car         [10] Add Place");
        System.out.println("  [11] Add Tour       [12] Add Taxi");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  DELETE");
        System.out.println("  [13] Delete Flight  [14] Delete Hotel");
        System.out.println("  [15] Delete Car     [16] Delete Place");
        System.out.println("  [17] Delete Tour    [18] Delete Taxi");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  USERS");
        System.out.println("  [19] List Users     [20] Delete User");
        System.out.println("  [21] Change User Role");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  DATABASE");
        System.out.println("  [23] Shutdown DB    [24] Restart DB");
        System.out.println("──────────────────────────────────────────────");
        System.out.println("  [22] Logout");
        System.out.println("  [0] Exit");
    }

    // ── Input dispatcher ─────────────────────────────────────────────────────

    private void processInput(String input) {
        String cmdStr;

        // Map number shortcuts to commands based on role
        if (input.matches("\\d+")) {
            cmdStr = numberToCommand(input);
            if (cmdStr == null) { System.out.println("Invalid choice."); return; }
        } else {
            cmdStr = input.toUpperCase();
        }

        Command command;
        try {
            command = Command.valueOf(cmdStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown command: " + cmdStr);
            return;
        }

        Map<String, String> params = new HashMap<>();

        switch (command) {
            case LOGIN     -> { params.put("username", prompt("Username")); params.put("password", prompt("Password")); }
            case REGISTER  -> { params.put("username", prompt("Username")); params.put("email", prompt("Email"));
                                params.put("password", prompt("Password")); params.put("fullName", prompt("Full name")); params.put("phone", prompt("Phone")); }
            case SEARCH_FLIGHTS -> { params.put("departure", prompt("Departure city")); params.put("destination", prompt("Destination city")); }
            case SEARCH_HOTELS  -> params.put("city", prompt("City"));
            case SEARCH_CARS    -> params.put("location", prompt("Location"));
            case SEARCH_PLACES  -> params.put("city", prompt("City"));
            case SEARCH_TAXIS   -> params.put("city", prompt("City"));
            case BOOK_FLIGHT    -> params.put("id", prompt("Flight ID"));
            case BOOK_HOTEL     -> params.put("id", prompt("Hotel ID"));
            case RENT_CAR       -> params.put("id", prompt("Car ID"));
            case BOOK_TAXI      -> params.put("id", prompt("Taxi ID"));
            case CANCEL_BOOKING -> params.put("id", prompt("Booking ID"));
            case DELETE_FLIGHT  -> params.put("id", prompt("Flight ID to delete"));
            case DELETE_HOTEL   -> params.put("id", prompt("Hotel ID to delete"));
            case DELETE_CAR     -> params.put("id", prompt("Car ID to delete"));
            case DELETE_PLACE   -> params.put("id", prompt("Place ID to delete"));
            case DELETE_TOUR    -> params.put("id", prompt("Tour ID to delete"));
            case DELETE_TAXI    -> params.put("id", prompt("Taxi ID to delete"));
            case DELETE_USER    -> params.put("id", prompt("User ID to delete"));
            case ADD_FLIGHT  -> promptFlight(params);
            case ADD_HOTEL   -> promptHotel(params);
            case ADD_CAR     -> promptCar(params);
            case ADD_PLACE   -> promptPlace(params);
            case ADD_TOUR    -> promptTour(params);
            case ADD_TAXI    -> promptTaxi(params);
            case UPDATE_USER_ROLE -> { params.put("id", prompt("User ID")); params.put("role", prompt("New role (VISITOR/USER/ADMIN)")); }
            default -> {}
        }

        try {
            Response resp = conn.send(command, params);
            handleResponse(command, resp);
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private String numberToCommand(String n) {
        return switch (currentRole) {
            case "VISITOR" -> switch (n) {
                case "1" -> "LOGIN";
                case "2" -> "REGISTER";
                case "3" -> "LIST_FLIGHTS";
                case "4" -> "LIST_HOTELS";
                case "5" -> "LIST_CARS";
                case "6" -> "LIST_PLACES";
                case "7" -> "LIST_TOURS";
                case "8" -> "LIST_TAXIS";
                default  -> null;
            };
            case "USER" -> switch (n) {
                case "1"  -> "LIST_FLIGHTS";
                case "2"  -> "LIST_HOTELS";
                case "3"  -> "LIST_CARS";
                case "4"  -> "LIST_PLACES";
                case "5"  -> "LIST_TOURS";
                case "6"  -> "LIST_TAXIS";
                case "7"  -> "SEARCH_FLIGHTS";
                case "8"  -> "SEARCH_HOTELS";
                case "9"  -> "SEARCH_CARS";
                case "10" -> "SEARCH_TAXIS";
                case "11" -> "BOOK_FLIGHT";
                case "12" -> "BOOK_HOTEL";
                case "13" -> "RENT_CAR";
                case "14" -> "BOOK_TAXI";
                case "15" -> "VIEW_BOOKINGS";
                case "16" -> "CANCEL_BOOKING";
                case "17" -> "LOGOUT";
                default   -> null;
            };
            case "ADMIN" -> switch (n) {
                case "1"  -> "LIST_FLIGHTS";
                case "2"  -> "LIST_HOTELS";
                case "3"  -> "LIST_CARS";
                case "4"  -> "LIST_PLACES";
                case "5"  -> "LIST_TOURS";
                case "6"  -> "LIST_TAXIS";
                case "7"  -> "ADD_FLIGHT";
                case "8"  -> "ADD_HOTEL";
                case "9"  -> "ADD_CAR";
                case "10" -> "ADD_PLACE";
                case "11" -> "ADD_TOUR";
                case "12" -> "ADD_TAXI";
                case "13" -> "DELETE_FLIGHT";
                case "14" -> "DELETE_HOTEL";
                case "15" -> "DELETE_CAR";
                case "16" -> "DELETE_PLACE";
                case "17" -> "DELETE_TOUR";
                case "18" -> "DELETE_TAXI";
                case "19" -> "LIST_USERS";
                case "20" -> "DELETE_USER";
                case "21" -> "UPDATE_USER_ROLE";
                case "22" -> "LOGOUT";
                case "23" -> "SHUTDOWN_DB";
                case "24" -> "RESTART_DB";
                default   -> null;
            };
            default -> null;
        };
    }

    // ── Response handler ─────────────────────────────────────────────────────

    private void handleResponse(Command command, Response resp) {
        System.out.println();
        if (resp.getStatus() == Response.Status.OK) {
            System.out.println("  [OK] " + resp.getMessage());

            if ((command == Command.LOGIN || command == Command.REGISTER) && resp.getData() != null) {
                JsonElement data = GSON.toJsonTree(resp.getData());
                if (data.isJsonObject()) {
                    if (data.getAsJsonObject().has("token"))
                        conn.setToken(data.getAsJsonObject().get("token").getAsString());
                    if (data.getAsJsonObject().has("role"))
                        currentRole = data.getAsJsonObject().get("role").getAsString();
                }
            }
            if (command == Command.LOGOUT) {
                conn.setToken(null);
                currentRole = "VISITOR";
            }
            if (resp.getData() != null) {
                System.out.println(GSON.toJson(resp.getData()));
            }
        } else {
            System.out.println("  [" + resp.getStatus() + "] " + resp.getMessage());
        }
    }

    // ── Prompt helpers ───────────────────────────────────────────────────────

    private String prompt(String label) {
        System.out.print("  " + label + ": ");
        return scanner.nextLine().trim();
    }

    private void promptFlight(Map<String, String> p) {
        p.put("flightNumber",  prompt("Flight number (e.g. AA101)"));
        p.put("departure",     prompt("Departure city"));
        p.put("destination",   prompt("Destination city"));
        p.put("departureTime", prompt("Departure time (HH:MM)"));
        p.put("arrivalTime",   prompt("Arrival time (HH:MM)"));
        p.put("price",         prompt("Price ($)"));
        p.put("seats",         prompt("Available seats"));
        p.put("airline",       prompt("Airline name"));
    }

    private void promptHotel(Map<String, String> p) {
        p.put("name",      prompt("Hotel name"));
        p.put("city",      prompt("City"));
        p.put("address",   prompt("Address"));
        p.put("stars",     prompt("Star rating (1-5)"));
        p.put("price",     prompt("Price per night ($)"));
        p.put("rooms",     prompt("Available rooms"));
        p.put("amenities", prompt("Amenities (e.g. WiFi, Pool, Gym)"));
    }

    private void promptCar(Map<String, String> p) {
        p.put("brand",        prompt("Brand (e.g. Toyota)"));
        p.put("model",        prompt("Model (e.g. Camry)"));
        p.put("type",         prompt("Type (Sedan/SUV/Truck)"));
        p.put("price",        prompt("Price per day ($)"));
        p.put("location",     prompt("Location"));
        p.put("seats",        prompt("Number of seats"));
        p.put("transmission", prompt("Transmission (Automatic/Manual)"));
    }

    private void promptPlace(Map<String, String> p) {
        p.put("name",        prompt("Place name"));
        p.put("city",        prompt("City"));
        p.put("country",     prompt("Country"));
        p.put("description", prompt("Description"));
        p.put("category",    prompt("Category (Museum/Park/Monument/Beach/Landmark)"));
        p.put("rating",      prompt("Rating (0.0 - 5.0)"));
        p.put("entryFee",    prompt("Entry fee ($, 0 if free)"));
    }

    private void promptTour(Map<String, String> p) {
        p.put("name",        prompt("Tour name"));
        p.put("destination", prompt("Destination"));
        p.put("duration",    prompt("Duration (days)"));
        p.put("price",       prompt("Price per person ($)"));
        p.put("description", prompt("Description"));
        p.put("groupSize",   prompt("Max group size"));
        p.put("guide",       prompt("Guide name"));
        p.put("schedule",    prompt("Schedule (e.g. Every Monday)"));
    }

    private void promptTaxi(Map<String, String> p) {
        p.put("driverName",   prompt("Driver name"));
        p.put("vehicleType",  prompt("Vehicle type (Sedan/Van/SUV)"));
        p.put("licensePlate", prompt("License plate"));
        p.put("city",         prompt("City"));
        p.put("pricePerKm",   prompt("Price per km ($)"));
        p.put("phone",        prompt("Phone number"));
        p.put("rating",       prompt("Rating (0.0 - 5.0)"));
    }
}
