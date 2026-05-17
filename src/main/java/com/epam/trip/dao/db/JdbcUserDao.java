package com.epam.trip.dao.db;

import com.epam.trip.auth.Role;
import com.epam.trip.entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserDao extends BaseJdbcDao<User> {

    public JdbcUserDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "users"; }

    @Override
    protected User fromResultSet(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setPhoneNumber(rs.getString("phone"));
        u.setRole(Role.valueOf(rs.getString("role")));
        return u;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO users (username,email,password,full_name,phone,role) VALUES (?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getFullName());
        ps.setString(5, u.getPhoneNumber());
        ps.setString(6, u.getRole().name());
    }

    @Override
    protected String updateSql() {
        return "UPDATE users SET username=?,email=?,password=?,full_name=?,phone=?,role=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getFullName());
        ps.setString(5, u.getPhoneNumber());
        ps.setString(6, u.getRole().name());
        ps.setLong(7, u.getId());
    }

    public Optional<User> findByUsername(String username) {
        return query("SELECT * FROM users WHERE username = ? COLLATE NOCASE", username)
                .stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return query("SELECT * FROM users WHERE email = ? COLLATE NOCASE", email)
                .stream().findFirst();
    }
}
