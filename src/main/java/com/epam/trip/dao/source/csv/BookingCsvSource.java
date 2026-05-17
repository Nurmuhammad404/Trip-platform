package com.epam.trip.dao.source.csv;

import java.util.List;

public class BookingCsvSource extends CsvDataSource {
    private static final String HEADER = "id,userId,serviceType,serviceId,bookingDate,status,totalPrice,customerName,customerEmail";

    public BookingCsvSource() {
        super("data/bookings.csv");
    }

    public BookingCsvSource(String filePath) {
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