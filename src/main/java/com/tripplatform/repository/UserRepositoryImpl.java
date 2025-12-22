package com.tripplatform.repository;

import com.tripplatform.model.User;
import java.util.List;
import java.util.ArrayList;

public class UserRepositoryImpl implements UserRepository {
    private List<User> users = new ArrayList<>();

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public User findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public void delete(int id) {
        users.removeIf(u -> u.getId() == id);
    }
}