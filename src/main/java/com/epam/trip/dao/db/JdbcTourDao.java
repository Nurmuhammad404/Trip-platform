package com.epam.trip.dao.db;

import com.epam.trip.entity.Tour;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTourDao extends BaseJdbcDao<Tour> {

    public JdbcTourDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "tours"; }

    @Override
    protected Tour fromResultSet(ResultSet rs) throws SQLException {
        Tour t = new Tour();
        t.setId(rs.getLong("id"));
        t.setName(rs.getString("name"));
        t.setDestination(rs.getString("destination"));
        t.setDuration(rs.getInt("duration"));
        t.setPrice(rs.getDouble("price"));
        t.setDescription(rs.getString("description"));
        t.setMaxGroupSize(rs.getInt("max_group_size"));
        t.setGuide(rs.getString("guide"));
        t.setSchedule(rs.getString("schedule"));
        return t;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO tours (name,destination,duration,price,description,max_group_size,guide,schedule) VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Tour t) throws SQLException {
        ps.setString(1, t.getName());
        ps.setString(2, t.getDestination());
        ps.setInt(3, t.getDuration());
        ps.setDouble(4, t.getPrice());
        ps.setString(5, t.getDescription());
        ps.setInt(6, t.getMaxGroupSize());
        ps.setString(7, t.getGuide());
        ps.setString(8, t.getSchedule());
    }

    @Override
    protected String updateSql() {
        return "UPDATE tours SET name=?,destination=?,duration=?,price=?,description=?,max_group_size=?,guide=?,schedule=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Tour t) throws SQLException {
        bindInsert(ps, t);
        ps.setLong(9, t.getId());
    }

    public List<Tour> findByDestination(String destination) {
        return query("SELECT * FROM tours WHERE destination LIKE ?", "%" + destination + "%");
    }
}
