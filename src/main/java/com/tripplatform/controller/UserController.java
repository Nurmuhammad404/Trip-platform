package com.tripplatform.controller;

import com.tripplatform.model.User;
import com.tripplatform.service.UserService;
import java.util.List;

public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void createUser(User user) {
        userService.createUser(user);
    }

    public User getUser(int id) {
        return userService.getUser(id);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public void deleteUser(int id) {
        userService.deleteUser(id);
    }
}