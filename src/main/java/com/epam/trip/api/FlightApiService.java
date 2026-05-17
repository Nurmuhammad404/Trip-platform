package com.epam.trip.api;

import com.epam.trip.entity.Flight;
import com.epam.trip.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Flight data from Aviationstack API with an in-memory cache.
 * Replaces the SQLite flights table — no local persistence for flights.
 */
public class FlightApiService {
    private static final Logger log = LoggerFactory.getLogger(FlightApiService.class);

    private final AviationstackClient client;
    private final Map<Long, Flight> cache = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(10_000);

    public FlightApiService(AviationstackClient client) {
        this.client = client;
    }

    public List<Flight> getAll() {
        try {
            List<Flight> flights = client.getAllFlights();
            updateCache(flights);
            return flights;
        } catch (ApiException e) {
            log.warn("Aviationstack API error — returning cached data: {}", e.getMessage());
            return new ArrayList<>(cache.values());
        }
    }

    public List<Flight> search(String departure, String destination) {
        try {
            List<Flight> flights = client.searchFlights(departure, destination);
            updateCache(flights);
            return flights;
        } catch (ApiException e) {
            log.warn("Aviationstack search error — filtering cache: {}", e.getMessage());
            return cache.values().stream()
                    .filter(f -> contains(f.getDeparture(), departure)
                              || contains(f.getDestination(), destination))
                    .toList();
        }
    }

    public Flight findById(long id) {
        return cache.get(id);
    }

    public Flight add(Flight f) {
        if (f.getId() == null || f.getId() == 0) {
            f.setId(idGen.incrementAndGet());
        }
        cache.put(f.getId(), f);
        return f;
    }

    public boolean remove(long id) {
        return cache.remove(id) != null;
    }

    private void updateCache(List<Flight> flights) {
        for (Flight f : flights) {
            if (f.getId() != null && f.getId() > 0) {
                cache.put(f.getId(), f);
            }
        }
    }

    private boolean contains(String field, String term) {
        return field != null && term != null
                && field.toLowerCase().contains(term.toLowerCase());
    }
}
