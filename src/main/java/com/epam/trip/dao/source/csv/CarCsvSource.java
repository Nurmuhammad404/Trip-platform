package com.epam.trip.dao.source.csv;

import java.util.List;

public class CarCsvSource extends CsvDataSource {
    private static final String HEADER = "id,brand,model,type,pricePerDay,available,location,seats,transmission";

    public CarCsvSource() {
        super("data/cars.csv");
    }

    public CarCsvSource(String filePath) {
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