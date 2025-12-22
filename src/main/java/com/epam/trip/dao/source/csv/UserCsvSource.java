package com.epam.trip.dao.source.csv;

import java.util.List;

public class UserCsvSource extends CsvDataSource {
    private static final String HEADER = "id,username,email,password,fullName,phoneNumber";

    public UserCsvSource() {
        super("data/users.csv");
    }

    public UserCsvSource(String filePath) {
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