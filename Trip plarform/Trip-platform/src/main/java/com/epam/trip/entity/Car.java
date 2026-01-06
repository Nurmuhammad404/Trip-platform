package com.epam.trip.entity;

import java.util.Objects;

public class Car extends BaseEntity {
    private String brand;
    private String model;
    private String type;
    private double pricePerDay;
    private boolean available;
    private String location;
    private int seats;
    private String transmission;

    public Car() {
        super();
    }

    public Car(Long id, String brand, String model, String type,
            double pricePerDay, boolean available, String location,
            int seats, String transmission) {
        super(id);
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.location = location;
        this.seats = seats;
        this.transmission = transmission;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Car car = (Car) o;
        return Objects.equals(brand, car.brand) &&
                Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), brand, model);
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", available=" + available +
                ", location='" + location + '\'' +
                ", seats=" + seats +
                ", transmission='" + transmission + '\'' +
                '}';
    }
}