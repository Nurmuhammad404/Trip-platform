package com.epam.trip.dao.source;

import java.util.List;

/**
 * Abstract hybrid data source that can switch between API and CSV sources.
 * Provides automatic fallback when API is unavailable or disabled.
 */
public abstract class HybridDataSource implements DataSource {

    protected final boolean apiEnabled;
    protected final DataSource csvSource;

    public HybridDataSource(boolean apiEnabled, DataSource csvSource) {
        this.apiEnabled = apiEnabled;
        this.csvSource = csvSource;
    }

    /**
     * Fetch data from API in CSV string array format.
     * Subclasses must implement this to convert API responses to String arrays.
     * 
     * @return List of String arrays (CSV rows) from API
     * @throws Exception if API fetch fails
     */
    protected abstract List<String[]> fetchFromApi() throws Exception;

    /**
     * Load all data. First tries API if enabled, falls back to CSV.
     */
    @Override
    public List<String[]> readAll() {
        if (apiEnabled) {
            try {
                List<String[]> apiData = fetchFromApi();
                if (apiData != null && !apiData.isEmpty()) {
                    return apiData;
                }
                // If API returns empty, fall back to CSV
            } catch (Exception e) {
                // Silently fall back to CSV when API fails
                // (Free tier APIs often have restrictions)
            }
        }

        // Fall back to CSV source
        return csvSource.readAll();
    }

    /**
     * Save all data. Always saves to CSV (API is read-only).
     */
    @Override
    public void writeAll(List<String[]> data) {
        csvSource.writeAll(data);
    }
}
