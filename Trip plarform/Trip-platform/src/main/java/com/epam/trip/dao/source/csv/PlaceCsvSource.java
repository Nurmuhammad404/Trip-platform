package com.epam.trip.dao.source.csv;

import java.util.List;

public class PlaceCsvSource extends CsvDataSource {
    private static final String HEADER = "id,name,city,country,description,category,rating,entryFee";

    public PlaceCsvSource() {
        super("data/places.csv");
    }

    public PlaceCsvSource(String filePath) {
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