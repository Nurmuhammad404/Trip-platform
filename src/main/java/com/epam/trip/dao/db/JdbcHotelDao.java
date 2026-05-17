package com.epam.trip.dao.db;

import com.epam.trip.entity.Hotel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcHotelDao extends BaseJdbcDao<Hotel> {

    public JdbcHotelDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "hotels"; }

    @Override
    protected Hotel fromResultSet(ResultSet rs) throws SQLException {
        Hotel h = new Hotel();
        h.setId(rs.getLong("id"));
        h.setName(rs.getString("name"));
        h.setCity(rs.getString("city"));
        h.setAddress(rs.getString("address"));
        h.setStarRating(rs.getInt("star_rating"));
        h.setPricePerNight(rs.getDouble("price_per_night"));
        h.setAvailableRooms(rs.getInt("available_rooms"));
        h.setAmenities(rs.getString("amenities"));
        return h;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO hotels (name,city,address,star_rating,price_per_night,available_rooms,amenities) VALUES (?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Hotel h) throws SQLException {
        ps.setString(1, h.getName());
        ps.setString(2, h.getCity());
        ps.setString(3, h.getAddress());
        ps.setInt(4, h.getStarRating());
        ps.setDouble(5, h.getPricePerNight());
        ps.setInt(6, h.getAvailableRooms());
        ps.setString(7, h.getAmenities());
    }

    @Override
    protected String updateSql() {
        return "UPDATE hotels SET name=?,city=?,address=?,star_rating=?,price_per_night=?,available_rooms=?,amenities=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Hotel h) throws SQLException {
        bindInsert(ps, h);
        ps.setLong(8, h.getId());
    }

    public List<Hotel> findByCity(String city) {
        return query("SELECT * FROM hotels WHERE city LIKE ?", "%" + city + "%");
    }

    public List<Hotel> findAvailable() {
        return query("SELECT * FROM hotels WHERE available_rooms > 0");
    }
}
