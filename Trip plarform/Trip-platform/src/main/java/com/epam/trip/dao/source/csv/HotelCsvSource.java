package com.epam.trip.dao.source.csv;

import java.util.List;

public class HotelCsvSource extends CsvDataSource {
    private static final String HEADER = "id,name,city,address,starRating,pricePerNight,availableRooms,amenities";

    public HotelCsvSource() {
        super("data/hotels.csv");
    }

    public HotelCsvSource(String filePath) {
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