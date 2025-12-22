package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import java.util.List;

public class GenericDaoImpl<T> implements GenericDao<T> {
    @Override
    public T findById(Long id) {
        // TODO: Implement
        return null;
    }

    @Override
    public List<T> findAll() {
        // TODO: Implement
        return null;
    }

    @Override
    public void save(T entity) {
        // TODO: Implement
    }

    @Override
    public void update(T entity) {
        // TODO: Implement
    }

    @Override
    public void delete(Long id) {
        // TODO: Implement
    }
}