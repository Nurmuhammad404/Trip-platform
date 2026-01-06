package com.epam.trip.dao.source.csv;

import java.util.List;

public class TourCsvSource extends CsvDataSource {
    private static final String HEADER = "id,name,destination,duration,price,description,maxGroupSize,guide,schedule";

    public TourCsvSource() {
        super("data/tours.csv");
    }

    public TourCsvSource(String filePath) {
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