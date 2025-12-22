package com.epam.trip.dao.source.csv;

import java.util.List;

public class FlightCsvSource extends CsvDataSource {
    private static final String HEADER = "id,flightNumber,departure,destination,departureTime,arrivalTime,price,availableSeats,airline";

    public FlightCsvSource() {
        super("data/flights.csv");
    }

    public FlightCsvSource(String filePath) {
        super(filePath);
    }

    @Override
    public List<String[]> readAll() {
        return readCsvFile();
    }

    @Override
    public void writeAll(List<String[]> data) {
        writeCsvFile(data, HEADER);
    }
}