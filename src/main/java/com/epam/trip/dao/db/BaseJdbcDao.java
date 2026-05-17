package com.epam.trip.dao.db;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.BaseEntity;
import com.epam.trip.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract JDBC base for all entity DAOs.
 * Subclasses provide the SQL and the mapping between ResultSet ↔ entity.
 */
public abstract class BaseJdbcDao<T extends BaseEntity> implements GenericDao<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final DatabaseManager db;

    protected BaseJdbcDao(DatabaseManager db) {
        this.db = db;
    }

    protected abstract String getTable();
    protected abstract T fromResultSet(ResultSet rs) throws SQLException;
    protected abstract void bindInsert(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void bindUpdate(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String insertSql();
    protected abstract String updateSql();

    @Override
    public T findById(Long id) {
        String sql = "SELECT * FROM " + getTable() + " WHERE id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("findById failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTable();
        List<T> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) result.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("findAll failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return result;
    }

    @Override
    public T save(T entity) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(insertSql(),
                    Statement.RETURN_GENERATED_KEYS)) {
                bindInsert(ps, entity);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) entity.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("save failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return entity;
    }

    @Override
    public T update(T entity) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(updateSql())) {
                bindUpdate(ps, entity);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DaoException("update failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return entity;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM " + getTable() + " WHERE id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DaoException("delete failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    /** Helper for typed list queries (search methods in subclasses). */
    protected List<T> query(String sql, Object... args) {
        List<T> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) result.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("query failed on " + getTable(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return result;
    }
}
