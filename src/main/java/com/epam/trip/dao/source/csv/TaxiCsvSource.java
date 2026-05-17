package com.epam.trip.dao.source.csv;

import java.util.List;

public class TaxiCsvSource extends CsvDataSource {
    private static final String HEADER = "id,driverName,vehicleType,licensePlate,city,pricePerKm,available,phoneNumber,rating";

    public TaxiCsvSource() {
        super("data/taxis.csv");
    }

    public TaxiCsvSource(String filePath) {
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