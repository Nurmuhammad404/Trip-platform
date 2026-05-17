package com.epam.trip.service;

import com.epam.trip.entity.Hotel;
import java.util.List;

public interface HotelService {
    Hotel findById(Long id);
    List<Hotel> findAll();
    void save(Hotel hotel);
    void update(Hotel hotel);
    void delete(Long id);
    // Additional business methods
}