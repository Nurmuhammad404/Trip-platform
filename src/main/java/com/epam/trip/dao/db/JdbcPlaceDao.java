package com.epam.trip.dao.db;

import com.epam.trip.entity.Place;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcPlaceDao extends BaseJdbcDao<Place> {

    public JdbcPlaceDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "places"; }

    @Override
    protected Place fromResultSet(ResultSet rs) throws SQLException {
        Place p = new Place();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setCity(rs.getString("city"));
        p.setCountry(rs.getString("country"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setRating(rs.getDouble("rating"));
        p.setEntryFee(rs.getDouble("entry_fee"));
        return p;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO places (name,city,country,description,category,rating,entry_fee) VALUES (?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Place p) throws SQLException {
        ps.setString(1, p.getName());
        ps.setString(2, p.getCity());
        ps.setString(3, p.getCountry());
        ps.setString(4, p.getDescription());
        ps.setString(5, p.getCategory());
        ps.setDouble(6, p.getRating());
        ps.setDouble(7, p.getEntryFee());
    }

    @Override
    protected String updateSql() {
        return "UPDATE places SET name=?,city=?,country=?,description=?,category=?,rating=?,entry_fee=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Place p) throws SQLException {
        bindInsert(ps, p);
        ps.setLong(8, p.getId());
    }

    public List<Place> findByCity(String city) {
        return query("SELECT * FROM places WHERE city LIKE ?", "%" + city + "%");
    }

    public List<Place> findByCategory(String category) {
        return query("SELECT * FROM places WHERE category LIKE ?", "%" + category + "%");
    }
}
