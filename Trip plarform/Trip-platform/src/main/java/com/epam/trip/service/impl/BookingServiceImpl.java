package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Booking;
import com.epam.trip.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    private final GenericDao<Booking> bookingDao;

    public BookingServiceImpl() {
        this.bookingDao = DaoFactory.getBookingDao();
    }

    public BookingServiceImpl(GenericDao<Booking> bookingDao) {
        this.bookingDao = bookingDao;
    }

    @Override
    public Booking findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        return bookingDao.findById(id);
    }

    @Override
    public List<Booking> findAll() {
        return bookingDao.findAll();
    }

    @Override
    public void save(Booking booking) {
        validateBooking(booking);
        if (booking.getStatus() == null || booking.getStatus().isEmpty()) {
            booking.setStatus("PENDING");
        }
        bookingDao.save(booking);
    }

    @Override
    public void update(Booking booking) {
        if (booking.getId() == null) {
            throw new IllegalArgumentException("Booking ID cannot be null for update");
        }
        validateBooking(booking);
        bookingDao.update(booking);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        bookingDao.delete(id);
    }

    public List<Booking> findByUserId(Long userId) {
        return bookingDao.findAll().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> findByStatus(String status) {
        return bookingDao.findAll().stream()
                .filter(b -> b.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Booking> findByServiceType(String serviceType) {
        return bookingDao.findAll().stream()
                .filter(b -> b.getServiceType().equalsIgnoreCase(serviceType))
                .collect(Collectors.toList());
    }

    public boolean confirmBooking(Long bookingId) {
        Booking booking = findById(bookingId);
        if (booking != null && "PENDING".equals(booking.getStatus())) {
            booking.setStatus("CONFIRMED");
            bookingDao.update(booking);
            return true;
        }
        return false;
    }

    public boolean cancelBooking(Long bookingId) {
        Booking booking = findById(bookingId);
        if (booking != null && !"CANCELLED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            bookingDao.update(booking);
            return true;
        }
        return false;
    }

    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        if (booking.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (booking.getServiceType() == null || booking.getServiceType().trim().isEmpty()) {
            throw new IllegalArgumentException("Service type is required");
        }
        if (booking.getServiceId() == null) {
            throw new IllegalArgumentException("Service ID is required");
        }
        if (booking.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
    }
}