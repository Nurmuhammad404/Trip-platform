package com.epam.trip.api;

import com.epam.trip.entity.Flight;
import com.epam.trip.entity.Place;
import com.epam.trip.exception.ApiException;

import java.util.List;

/**
 * Simple test to verify API connectivity and functionality.
 * Run this to check if Aviationstack and OpenRouteService APIs are working.
 */
public class ApiTest {

    // API Keys from app.properties
    private static final String AVIATIONSTACK_KEY = "b25e81fd169e95734032fb6f6651b55c";
    private static final String AVIATIONSTACK_URL = "http://api.aviationstack.com/v1";

    private static final String OPENROUTE_KEY = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6ImVmOWZhMjc3YzE5ZjQwMTFhMTc0OTE3YmI0ODQ4M2M0IiwiaCI6Im11cm11cjY0In0=";
    private static final String OPENROUTE_URL = "https://api.openrouteservice.org";

    private static final int TIMEOUT_SECONDS = 30;
    private static final int RETRY_COUNT = 2;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("API Connectivity Test");
        System.out.println("=================================================\n");

        testAviationstackAPI();
        System.out.println();
        testOpenRouteServiceAPI();

        System.out.println("\n=================================================");
        System.out.println("Test Complete");
        System.out.println("=================================================");
    }

    private static void testAviationstackAPI() {
        System.out.println("Testing Aviationstack Flight API...");
        System.out.println("-------------------------------------------------");

        try {
            AviationstackClient client = new AviationstackClient(
                    AVIATIONSTACK_KEY,
                    AVIATIONSTACK_URL,
                    TIMEOUT_SECONDS,
                    RETRY_COUNT);

            System.out.println("Fetching flight data...");
            List<Flight> flights = client.getAllFlights();

            if (flights != null && !flights.isEmpty()) {
                System.out.println("✓ SUCCESS: API is working!");
                System.out.println("  Retrieved " + flights.size() + " flights");

                // Show first few flights
                int displayCount = Math.min(3, flights.size());
                System.out.println("\nSample flights:");
                for (int i = 0; i < displayCount; i++) {
                    Flight f = flights.get(i);
                    System.out.println("  " + (i + 1) + ". " + f.getFlightNumber() +
                            " (" + f.getAirline() + ") - " +
                            f.getDeparture() + " → " + f.getDestination());
                }
            } else {
                System.out.println("⚠ WARNING: API returned no data");
                System.out.println("  This might be normal if there are no current flights");
            }

        } catch (ApiException e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("  Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: Unexpected error");
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testOpenRouteServiceAPI() {
        System.out.println("Testing OpenRouteService Places API...");
        System.out.println("-------------------------------------------------");

        try {
            OpenRouteServiceClient client = new OpenRouteServiceClient(
                    OPENROUTE_KEY,
                    OPENROUTE_URL,
                    TIMEOUT_SECONDS,
                    RETRY_COUNT);

            System.out.println("Fetching places data...");
            List<Place> places = client.getAllPlaces();

            if (places != null && !places.isEmpty()) {
                System.out.println("✓ SUCCESS: API is working!");
                System.out.println("  Retrieved " + places.size() + " places");

                // Show first few places
                int displayCount = Math.min(3, places.size());
                System.out.println("\nSample places:");
                for (int i = 0; i < displayCount; i++) {
                    Place p = places.get(i);
                    System.out.println("  " + (i + 1) + ". " + p.getName() +
                            " (" + p.getCategory() + ") - " +
                            p.getCity() + ", " + p.getCountry());
                }
            } else {
                System.out.println("⚠ WARNING: API returned no data");
                System.out.println("  This might indicate an API configuration issue");
            }

        } catch (ApiException e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("  Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.out.println("✗ ERROR: Unexpected error");
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
