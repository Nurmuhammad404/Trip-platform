package com.epam.trip.auth;

import com.epam.trip.entity.User;

public interface AuthService {
    Session login(String username, String password);
    void logout(String token);
    Session register(String username, String email, String password, String fullName, String phone);
    User getUser(String token);
}
