package com.tripplatform.service;

import com.tripplatform.model.User;
import com.tripplatform.repository.UserRepository;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUser(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(User user) {
        // TODO: implement update logic
    }

    @Override
    public void deleteUser(int id) {
        userRepository.delete(id);
    }
}