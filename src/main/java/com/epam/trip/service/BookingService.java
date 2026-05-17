package com.epam.trip.service;

import com.epam.trip.entity.Booking;
import java.util.List;

public interface BookingService {
    Booking findById(Long id);
    List<Booking> findAll();
    void save(Booking booking);
    void update(Booking booking);
    void delete(Long id);
    // Additional business methods
}