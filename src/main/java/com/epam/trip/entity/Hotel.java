package com.epam.trip.entity;

import java.util.Objects;

public class Hotel extends BaseEntity {
    private String name;
    private String city;
    private String address;
    private int starRating;
    private double pricePerNight;
    private int availableRooms;
    private String amenities;

    public Hotel() {
        super();
    }

    public Hotel(Long id, String name, String city, String address,
            int starRating, double pricePerNight, int availableRooms, String amenities) {
        super(id);
        this.name = name;
        this.city = city;
        this.address = address;
        this.starRating = starRating;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
        this.amenities = amenities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(name, hotel.name) &&
                Objects.equals(city, hotel.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, city);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", starRating=" + starRating +
                ", pricePerNight=" + pricePerNight +
                ", availableRooms=" + availableRooms +
                ", amenities='" + amenities + '\'' +
                '}';
    }
}