package com.epam.trip.service;

import com.epam.trip.entity.User;
import java.util.List;

public interface UserService {
    User findById(Long id);
    List<User> findAll();
    void save(User user);
    void update(User user);
    void delete(Long id);
    // Additional business methods
}