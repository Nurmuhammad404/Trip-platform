package com.epam.trip.dao.db;

import com.epam.trip.entity.Flight;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcFlightDao extends BaseJdbcDao<Flight> {

    public JdbcFlightDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "flights"; }

    @Override
    protected Flight fromResultSet(ResultSet rs) throws SQLException {
        Flight f = new Flight();
        f.setId(rs.getLong("id"));
        f.setFlightNumber(rs.getString("flight_number"));
        f.setDeparture(rs.getString("departure"));
        f.setDestination(rs.getString("destination"));
        f.setDepartureTime(rs.getString("departure_time"));
        f.setArrivalTime(rs.getString("arrival_time"));
        f.setPrice(rs.getDouble("price"));
        f.setAvailableSeats(rs.getInt("available_seats"));
        f.setAirline(rs.getString("airline"));
        return f;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO flights (flight_number,departure,destination,departure_time,arrival_time,price,available_seats,airline) VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Flight f) throws SQLException {
        ps.setString(1, f.getFlightNumber());
        ps.setString(2, f.getDeparture());
        ps.setString(3, f.getDestination());
        ps.setString(4, f.getDepartureTime());
        ps.setString(5, f.getArrivalTime());
        ps.setDouble(6, f.getPrice());
        ps.setInt(7, f.getAvailableSeats());
        ps.setString(8, f.getAirline());
    }

    @Override
    protected String updateSql() {
        return "UPDATE flights SET flight_number=?,departure=?,destination=?,departure_time=?,arrival_time=?,price=?,available_seats=?,airline=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Flight f) throws SQLException {
        bindInsert(ps, f);
        ps.setLong(9, f.getId());
    }

    public List<Flight> findByRoute(String departure, String destination) {
        return query("SELECT * FROM flights WHERE departure LIKE ? AND destination LIKE ?",
                "%" + departure + "%", "%" + destination + "%");
    }

    public List<Flight> findAvailable() {
        return query("SELECT * FROM flights WHERE available_seats > 0");
    }
}
