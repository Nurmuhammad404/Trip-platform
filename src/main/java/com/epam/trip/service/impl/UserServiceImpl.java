package com.epam.trip.service.impl;

import com.epam.trip.dao.DaoFactory;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.User;
import com.epam.trip.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    private final GenericDao<User> userDao;

    public UserServiceImpl() {
        this.userDao = DaoFactory.getUserDao();
    }

    public UserServiceImpl(GenericDao<User> userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void save(User user) {
        validateUser(user);
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        validateUser(user);
        userDao.update(user);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userDao.delete(id);
    }

    public User findByUsername(String username) {
        return userDao.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User findByEmail(String email) {
        return userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public boolean authenticate(String username, String password) {
        User user = findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}