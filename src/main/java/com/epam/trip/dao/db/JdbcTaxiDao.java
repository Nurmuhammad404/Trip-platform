package com.epam.trip.dao.db;

import com.epam.trip.entity.Taxi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTaxiDao extends BaseJdbcDao<Taxi> {

    public JdbcTaxiDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "taxis"; }

    @Override
    protected Taxi fromResultSet(ResultSet rs) throws SQLException {
        Taxi t = new Taxi();
        t.setId(rs.getLong("id"));
        t.setDriverName(rs.getString("driver_name"));
        t.setVehicleType(rs.getString("vehicle_type"));
        t.setLicensePlate(rs.getString("license_plate"));
        t.setCity(rs.getString("city"));
        t.setPricePerKm(rs.getDouble("price_per_km"));
        t.setAvailable(rs.getInt("available") == 1);
        t.setPhoneNumber(rs.getString("phone"));
        t.setRating(rs.getDouble("rating"));
        return t;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO taxis (driver_name,vehicle_type,license_plate,city,price_per_km,available,phone,rating) VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Taxi t) throws SQLException {
        ps.setString(1, t.getDriverName());
        ps.setString(2, t.getVehicleType());
        ps.setString(3, t.getLicensePlate());
        ps.setString(4, t.getCity());
        ps.setDouble(5, t.getPricePerKm());
        ps.setInt(6, t.isAvailable() ? 1 : 0);
        ps.setString(7, t.getPhoneNumber());
        ps.setDouble(8, t.getRating());
    }

    @Override
    protected String updateSql() {
        return "UPDATE taxis SET driver_name=?,vehicle_type=?,license_plate=?,city=?,price_per_km=?,available=?,phone=?,rating=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Taxi t) throws SQLException {
        bindInsert(ps, t);
        ps.setLong(9, t.getId());
    }

    public List<Taxi> findByCity(String city) {
        return query("SELECT * FROM taxis WHERE city LIKE ?", "%" + city + "%");
    }

    public List<Taxi> findAvailable() {
        return query("SELECT * FROM taxis WHERE available = 1");
    }
}
