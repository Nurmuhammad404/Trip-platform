package com.epam.trip.dao.source;

import java.util.List;

public interface DataSource {
    List<String[]> readAll();
    void writeAll(List<String[]> data);
}