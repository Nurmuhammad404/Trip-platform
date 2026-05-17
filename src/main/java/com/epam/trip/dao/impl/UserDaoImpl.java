package com.epam.trip.dao.impl;

import com.epam.trip.auth.Role;
import com.epam.trip.dao.GenericDao;
import com.epam.trip.dao.source.csv.UserCsvSource;
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
        cache = rows.stream().map(this::fromCsv).filter(u -> u != null).collect(Collectors.toList());
    }

    private void saveData() {
        List<String[]> rows = cache.stream().map(this::toCsv).collect(Collectors.toList());
        dataSource.writeAll(rows);
    }

    private User fromCsv(String[] row) {
        if (row.length < 6) return null;
        User u = new User();
        u.setId(Long.parseLong(row[0].trim()));
        u.setUsername(row[1].trim());
        u.setEmail(row[2].trim());
        u.setPassword(row[3].trim());
        u.setFullName(row[4].trim());
        u.setPhoneNumber(row[5].trim());
        u.setRole(row.length > 6 ? Role.valueOf(row[6].trim()) : Role.USER);
        return u;
    }

    private String[] toCsv(User u) {
        return new String[]{
            u.getId().toString(), u.getUsername(), u.getEmail(),
            u.getPassword(), u.getFullName(), u.getPhoneNumber(),
            u.getRole().name()
        };
    }

    @Override
    public User findById(Long id) {
        return cache.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() { return new ArrayList<>(cache); }

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            long maxId = cache.stream().mapToLong(User::getId).max().orElse(0L);
            entity.setId(maxId + 1);
        }
        cache.add(entity);
        saveData();
        return entity;
    }

    @Override
    public User update(User entity) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(entity.getId())) {
                cache.set(i, entity);
                saveData();
                return entity;
            }
        }
        return entity;
    }

    @Override
    public void delete(Long id) {
        cache.removeIf(u -> u.getId().equals(id));
        saveData();
    }

    public User findByUsername(String username) {
        return cache.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }
}
