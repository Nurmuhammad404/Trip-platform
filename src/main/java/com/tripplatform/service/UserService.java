package com.tripplatform.service;

import com.tripplatform.model.User;
import java.util.List;

public interface UserService {
    void createUser(User user);
    User getUser(int id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int id);
}