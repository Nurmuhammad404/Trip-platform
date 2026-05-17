package com.epam.trip.dao.source.api;

import com.epam.trip.api.AviationstackClient;
import com.epam.trip.dao.source.DataSource;
import com.epam.trip.dao.source.HybridDataSource;
import com.epam.trip.entity.Flight;

import java.util.ArrayList;
import java.util.List;

/**
 * Hybrid data source for flights that fetches from Aviationstack API
 * and falls back to CSV when API is unavailable.
 */
public class FlightApiSource extends HybridDataSource {

    private final AviationstackClient apiClient;

    public FlightApiSource(boolean apiEnabled,
            DataSource csvSource,
            String apiKey,
            String apiUrl,
            int timeout,
            int retryCount) {
        super(apiEnabled, csvSource);
        this.apiClient = new AviationstackClient(apiKey, apiUrl, timeout, retryCount);
    }

    @Override
    protected List<String[]> fetchFromApi() throws Exception {
        // Fetch flights from API
        List<Flight> flights = apiClient.getAllFlights();

        // Convert to String array format for CSV compatibility
        List<String[]> csvData = new ArrayList<>();
        for (Flight flight : flights) {
            String[] row = new String[] {
                    String.valueOf(flight.getId()),
                    flight.getFlightNumber(),
                    flight.getDeparture(),
                    flight.getDestination(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    String.valueOf(flight.getPrice()),
                    String.valueOf(flight.getAvailableSeats()),
                    flight.getAirline()
            };
            csvData.add(row);
        }

        return csvData;
    }
}
