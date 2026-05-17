package com.epam.trip.dao.source.api;

import com.epam.trip.api.OpenRouteServiceClient;
import com.epam.trip.dao.source.DataSource;
import com.epam.trip.dao.source.HybridDataSource;
import com.epam.trip.entity.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Hybrid data source for places that fetches from OpenRouteService API
 * and falls back to CSV when API is unavailable.
 */
public class PlaceApiSource extends HybridDataSource {

    private final OpenRouteServiceClient apiClient;

    public PlaceApiSource(boolean apiEnabled,
            DataSource csvSource,
            String apiKey,
            String apiUrl,
            int timeout,
            int retryCount) {
        super(apiEnabled, csvSource);
        this.apiClient = new OpenRouteServiceClient(apiKey, apiUrl, timeout, retryCount);
    }

    @Override
    protected List<String[]> fetchFromApi() throws Exception {
        // Fetch places from API
        List<Place> places = apiClient.getAllPlaces();

        // Convert to String array format for CSV compatibility
        List<String[]> csvData = new ArrayList<>();
        for (Place place : places) {
            String[] row = new String[] {
                    String.valueOf(place.getId()),
                    place.getName(),
                    place.getCity(),
                    place.getCountry(),
                    place.getDescription(),
                    place.getCategory(),
                    String.valueOf(place.getRating()),
                    String.valueOf(place.getEntryFee())
            };
            csvData.add(row);
        }

        return csvData;
    }
}
