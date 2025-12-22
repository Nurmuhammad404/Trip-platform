package com.epam.trip.api;

import com.epam.trip.entity.Flight;
import com.epam.trip.exception.ApiException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Client for Aviationstack Flight API.
 * Provides access to real-time flight data.
 */
public class AviationstackClient {

    private final ApiClient apiClient;
    private final String apiKey;
    private final String baseUrl;

    public AviationstackClient(String apiKey, String baseUrl, int timeoutSeconds, int retryCount) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.apiClient = new ApiClient(timeoutSeconds, retryCount);
    }

    /**
     * Search for flights between departure and destination cities.
     * Note: Free tier of Aviationstack only provides historical and current data,
     * not future schedules. This searches current/recent flights.
     * 
     * @param departure   Departure city/airport code
     * @param destination Destination city/airport code
     * @return List of Flight entities
     * @throws ApiException if API request fails
     */
    public List<Flight> searchFlights(String departure, String destination) throws ApiException {
        try {
            // Build URL with query parameters
            String url = String.format("%s/flights?access_key=%s&dep_iata=%s&arr_iata=%s&limit=50",
                    baseUrl, apiKey, departure, destination);

            String response = apiClient.get(url);
            return parseFlightsResponse(response);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.invalidResponse("Failed to parse flight data: " + e.getMessage());
        }
    }

    /**
     * Get all available flights (uses routes endpoint in free tier).
     * 
     * @return List of Flight entities
     * @throws ApiException if API request fails
     */
    public List<Flight> getAllFlights() throws ApiException {
        try {
            String url = String.format("%s/routes?access_key=%s&limit=100", baseUrl, apiKey);
            String response = apiClient.get(url);
            return parseRoutesResponse(response);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.invalidResponse("Failed to parse routes data: " + e.getMessage());
        }
    }

    /**
     * Parse flight data from Aviationstack API response.
     */
    private List<Flight> parseFlightsResponse(String jsonResponse) {
        List<Flight> flights = new ArrayList<>();

        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        if (data == null || data.size() == 0) {
            return flights; // No flights found
        }

        int id = 1;
        for (JsonElement element : data) {
            try {
                JsonObject flightObj = element.getAsJsonObject();
                JsonObject flightData = flightObj.getAsJsonObject("flight");
                JsonObject departure = flightObj.getAsJsonObject("departure");
                JsonObject arrival = flightObj.getAsJsonObject("arrival");
                JsonObject airline = flightObj.getAsJsonObject("airline");

                if (flightData == null || departure == null || arrival == null) {
                    continue;
                }

                Flight flight = new Flight();
                flight.setId((long) id++);
                flight.setFlightNumber(getStringValue(flightData, "iata", "FLIGHT" + id));
                flight.setDeparture(getStringValue(departure, "airport", "Unknown"));
                flight.setDestination(getStringValue(arrival, "airport", "Unknown"));
                flight.setDepartureTime(getStringValue(departure, "scheduled", "00:00"));
                flight.setArrivalTime(getStringValue(arrival, "scheduled", "00:00"));
                flight.setAirline(
                        airline != null ? getStringValue(airline, "name", "Unknown Airline") : "Unknown Airline");

                // Aviationstack free tier doesn't provide price/seats, use defaults
                flight.setPrice(299.99);
                flight.setAvailableSeats(50);

                flights.add(flight);
            } catch (Exception e) {
                // Skip invalid flight entries
                continue;
            }
        }

        return flights;
    }

    /**
     * Parse routes data (for getAllFlights in free tier).
     */
    private List<Flight> parseRoutesResponse(String jsonResponse) {
        List<Flight> flights = new ArrayList<>();

        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        if (data == null || data.size() == 0) {
            return flights;
        }

        int id = 1;
        for (JsonElement element : data) {
            try {
                JsonObject routeObj = element.getAsJsonObject();
                JsonObject airline = routeObj.getAsJsonObject("airline");
                JsonObject depAirport = routeObj.getAsJsonObject("departure_airport");
                JsonObject arrAirport = routeObj.getAsJsonObject("arrival_airport");

                if (depAirport == null || arrAirport == null) {
                    continue;
                }

                Flight flight = new Flight();
                flight.setId((long) id++);
                flight.setFlightNumber(getStringValue(airline, "iata", "XX") + id);
                flight.setDeparture(getStringValue(depAirport, "name", "Unknown"));
                flight.setDestination(getStringValue(arrAirport, "name", "Unknown"));
                flight.setAirline(
                        airline != null ? getStringValue(airline, "name", "Unknown Airline") : "Unknown Airline");

                // Set default times and prices for routes
                flight.setDepartureTime("10:00");
                flight.setArrivalTime("14:00");
                flight.setPrice(299.99);
                flight.setAvailableSeats(45);

                flights.add(flight);
            } catch (Exception e) {
                continue;
            }
        }

        return flights;
    }

    /**
     * Safely get string value from JSON object.
     */
    private String getStringValue(JsonObject obj, String key, String defaultValue) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }
}
