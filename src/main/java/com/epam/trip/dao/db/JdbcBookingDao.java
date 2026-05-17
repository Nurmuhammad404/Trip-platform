package com.epam.trip.dao.db;

import com.epam.trip.entity.Booking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcBookingDao extends BaseJdbcDao<Booking> {

    public JdbcBookingDao(DatabaseManager db) { super(db); }

    @Override protected String getTable() { return "bookings"; }

    @Override
    protected Booking fromResultSet(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getLong("id"));
        b.setUserId(rs.getLong("user_id"));
        b.setServiceType(rs.getString("service_type"));
        b.setServiceId(rs.getLong("service_id"));
        b.setBookingDate(rs.getString("booking_date"));
        b.setStatus(rs.getString("status"));
        b.setTotalPrice(rs.getDouble("total_price"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerEmail(rs.getString("customer_email"));
        return b;
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO bookings (user_id,service_type,service_id,booking_date,status,total_price,customer_name,customer_email) VALUES (?,?,?,?,?,?,?,?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Booking b) throws SQLException {
        ps.setLong(1, b.getUserId());
        ps.setString(2, b.getServiceType());
        ps.setLong(3, b.getServiceId());
        ps.setString(4, b.getBookingDate());
        ps.setString(5, b.getStatus() != null ? b.getStatus() : "PENDING");
        ps.setDouble(6, b.getTotalPrice());
        ps.setString(7, b.getCustomerName());
        ps.setString(8, b.getCustomerEmail());
    }

    @Override
    protected String updateSql() {
        return "UPDATE bookings SET user_id=?,service_type=?,service_id=?,booking_date=?,status=?,total_price=?,customer_name=?,customer_email=? WHERE id=?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Booking b) throws SQLException {
        bindInsert(ps, b);
        ps.setLong(9, b.getId());
    }

    public List<Booking> findByUserId(Long userId) {
        return query("SELECT * FROM bookings WHERE user_id = ?", userId);
    }

    public List<Booking> findByStatus(String status) {
        return query("SELECT * FROM bookings WHERE status = ?", status);
    }
}
