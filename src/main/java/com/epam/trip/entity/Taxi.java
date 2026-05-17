package com.epam.trip.entity;

import java.util.Objects;

public class Taxi extends BaseEntity {
    private String driverName;
    private String vehicleType;
    private String licensePlate;
    private String city;
    private double pricePerKm;
    private boolean available;
    private String phoneNumber;
    private double rating;

    public Taxi() {
        super();
    }

    public Taxi(Long id, String driverName, String vehicleType, String licensePlate,
            String city, double pricePerKm, boolean available,
            String phoneNumber, double rating) {
        super(id);
        this.driverName = driverName;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.city = city;
        this.pricePerKm = pricePerKm;
        this.available = available;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Taxi taxi = (Taxi) o;
        return Objects.equals(licensePlate, taxi.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licensePlate);
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                ", driverName='" + driverName + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", city='" + city + '\'' +
                ", pricePerKm=" + pricePerKm +
                ", available=" + available +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                '}';
    }
}