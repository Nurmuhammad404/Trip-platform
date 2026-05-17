package com.epam.trip.dao.impl;

import com.epam.trip.dao.GenericDao;
import java.util.List;

public class GenericDaoImpl<T> implements GenericDao<T> {
    @Override public T findById(Long id) { return null; }
    @Override public List<T> findAll() { return null; }
    @Override public T save(T entity) { return entity; }
    @Override public T update(T entity) { return entity; }
    @Override public void delete(Long id) {}
}
