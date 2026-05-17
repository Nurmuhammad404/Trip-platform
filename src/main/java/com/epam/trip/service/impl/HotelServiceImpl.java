package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Hotel;
import com.epam.trip.service.HotelService;

import java.util.List;
import java.util.stream.Collectors;

public class HotelServiceImpl implements HotelService {
    private final GenericDao<Hotel> hotelDao;

    public HotelServiceImpl() {
        this.hotelDao = DaoFactory.getHotelDao();
    }

    public HotelServiceImpl(GenericDao<Hotel> hotelDao) {
        this.hotelDao = hotelDao;
    }

    @Override
    public Hotel findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid hotel ID");
        }
        return hotelDao.findById(id);
    }

    @Override
    public List<Hotel> findAll() {
        return hotelDao.findAll();
    }

    @Override
    public void save(Hotel hotel) {
        validateHotel(hotel);
        hotelDao.save(hotel);
    }

    @Override
    public void update(Hotel hotel) {
        if (hotel.getId() == null) {
            throw new IllegalArgumentException("Hotel ID cannot be null for update");
        }
        validateHotel(hotel);
        hotelDao.update(hotel);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid hotel ID");
        }
        hotelDao.delete(id);
    }

    public List<Hotel> searchByCity(String city) {
        return hotelDao.findAll().stream()
                .filter(h -> h.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Hotel> searchByPriceRange(double minPrice, double maxPrice) {
        return hotelDao.findAll().stream()
                .filter(h -> h.getPricePerNight() >= minPrice && h.getPricePerNight() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Hotel> searchByStarRating(int minRating) {
        return hotelDao.findAll().stream()
                .filter(h -> h.getStarRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Hotel> getAvailableHotels() {
        return hotelDao.findAll().stream()
                .filter(h -> h.getAvailableRooms() > 0)
                .collect(Collectors.toList());
    }

    public boolean bookRoom(Long hotelId) {
        Hotel hotel = findById(hotelId);
        if (hotel != null && hotel.getAvailableRooms() > 0) {
            hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
            hotelDao.update(hotel);
            return true;
        }
        return false;
    }

    private void validateHotel(Hotel hotel) {
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel cannot be null");
        }
        if (hotel.getName() == null || hotel.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel name is required");
        }
        if (hotel.getCity() == null || hotel.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (hotel.getPricePerNight() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (hotel.getStarRating() < 0 || hotel.getStarRating() > 5) {
            throw new IllegalArgumentException("Star rating must be between 0 and 5");
        }
    }
}