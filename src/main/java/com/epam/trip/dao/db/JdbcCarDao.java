package com.epam.trip.dao.db;

import com.epam.trip.entity.Car;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcCarDao extends BaseJdbcDao<Car> {

    public JdbcCarDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "cars"; }

    @Override
    protected Car fromResultSet(ResultSet rs) throws SQLException {
        Car c = new Car();
        c.setId(rs.getLong("id"));
        c.setBrand(rs.getString("brand"));
        c.setModel(rs.getString("model"));
        c.setType(rs.getString("type"));
        c.setPricePerDay(rs.getDouble("price_per_day"));
        c.setAvailable(rs.getInt("available") == 1);
        c.setLocation(rs.getString("location"));
        c.setSeats(rs.getInt("seats"));
        c.setTransmission(rs.getString("transmission"));
        return c;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO cars (brand,model,type,price_per_day,available,location,seats,transmission) VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Car c) throws SQLException {
        ps.setString(1, c.getBrand());
        ps.setString(2, c.getModel());
        ps.setString(3, c.getType());
        ps.setDouble(4, c.getPricePerDay());
        ps.setInt(5, c.isAvailable() ? 1 : 0);
        ps.setString(6, c.getLocation());
        ps.setInt(7, c.getSeats());
        ps.setString(8, c.getTransmission());
    }

    @Override
    protected String updateSql() {
        return "UPDATE cars SET brand=?,model=?,type=?,price_per_day=?,available=?,location=?,seats=?,transmission=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Car c) throws SQLException {
        bindInsert(ps, c);
        ps.setLong(9, c.getId());
    }

    public List<Car> findAvailable() {
        return query("SELECT * FROM cars WHERE available = 1");
    }

    public List<Car> findByLocation(String location) {
        return query("SELECT * FROM cars WHERE location LIKE ?", "%" + location + "%");
    }
}
