package com.epam.trip.entity;

import java.util.Objects;

public class Booking extends BaseEntity {
    private Long userId;
    private String serviceType;
    private Long serviceId;
    private String bookingDate;
    private String status;
    private double totalPrice;
    private String customerName;
    private String customerEmail;

    public Booking() {
        super();
    }

    public Booking(Long id, Long userId, String serviceType, Long serviceId,
            String bookingDate, String status, double totalPrice,
            String customerName, String customerEmail) {
        super(id);
        this.userId = userId;
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Booking booking = (Booking) o;
        return Objects.equals(userId, booking.userId) &&
                Objects.equals(serviceType, booking.serviceType) &&
                Objects.equals(serviceId, booking.serviceId) &&
                Objects.equals(bookingDate, booking.bookingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, serviceType, serviceId, bookingDate);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", serviceType='" + serviceType + '\'' +
                ", serviceId=" + serviceId +
                ", bookingDate='" + bookingDate + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}