package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.*;
import com.epam.trip.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDaoImpl implements GenericDao<User> {
    private final UserCsvSource dataSource;
    private List<User> cache;

    public UserDaoImpl() {
        this.dataSource = new UserCsvSource();
        this.cache = new ArrayList<>();
        loadData();
    }

    public UserDaoImpl(UserCsvSource dataSource) {
        this.dataSource = dataSource;
        this.cache = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String[]> rows = dataSource.readAll();
        cache = rows.stream()
                .map(this::fromCsv)
                .collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private User fromCsv(String[] row) {
        if (row.length < 6)
            return null;
        return new User(
                Long.parseLong(row[0]),
                row[1],
                row[2],
                row[3],
                row[4],
                row[5]);
    }

    private String[] toCsv(User user) {
        return new String[] {
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getPhoneNumber()
        };
    }

    @Override
    public User findById(Long id) {
        return cache.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public void save(User entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
    }

    @Override
    public void update(User entity) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(entity.getId())) {
                cache.set(i, entity);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        cache.removeIf(user -> user.getId().equals(id));
        saveData();
    }
}
