package com.epam.trip.auth;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.User;
import com.epam.trip.exception.ServiceException;
import com.epam.trip.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final GenericDao<User> userDao;
    private final SessionManager sessionManager;

    public AuthServiceImpl(GenericDao<User> userDao) {
        this.userDao = userDao;
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public Session login(String username, String password) {
        if (username == null || password == null) {
            throw new ValidationException("Username and password are required");
        }
        User user = userDao.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Invalid credentials"));

        if (!hash(password).equals(user.getPassword())) {
            throw new ServiceException("Invalid credentials");
        }

        Session session = sessionManager.create(user.getId(), user.getUsername(), user.getRole());
        log.info("User '{}' logged in with role {}", username, user.getRole());
        return session;
    }

    @Override
    public void logout(String token) {
        sessionManager.invalidate(token);
        log.info("Session {} invalidated", token);
    }

    @Override
    public Session register(String username, String email, String password,
                            String fullName, String phone) {
        if (username == null || email == null || password == null) {
            throw new ValidationException("Username, email and password are required");
        }
        boolean taken = userDao.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username)
                        || u.getEmail().equalsIgnoreCase(email));
        if (taken) {
            throw new ServiceException("Username or email already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hash(password));
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setRole(Role.USER);
        userDao.save(user);

        log.info("New user registered: {}", username);
        return sessionManager.create(user.getId(), username, Role.USER);
    }

    @Override
    public User getUser(String token) {
        Session session = sessionManager.get(token);
        if (!session.isAuthenticated()) return null;
        return userDao.findById(session.getUserId());
    }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
