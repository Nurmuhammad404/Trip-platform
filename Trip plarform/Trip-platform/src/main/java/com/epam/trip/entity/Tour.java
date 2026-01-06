package com.epam.trip.entity;

import java.util.Objects;

public class Tour extends BaseEntity {
    private String name;
    private String destination;
    private int duration;
    private double price;
    private String description;
    private int maxGroupSize;
    private String guide;
    private String schedule;

    public Tour() {
        super();
    }

    public Tour(Long id, String name, String destination, int duration,
            double price, String description, int maxGroupSize,
            String guide, String schedule) {
        super(id);
        this.name = name;
        this.destination = destination;
        this.duration = duration;
        this.price = price;
        this.description = description;
        this.maxGroupSize = maxGroupSize;
        this.guide = guide;
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Tour tour = (Tour) o;
        return Objects.equals(name, tour.name) &&
                Objects.equals(destination, tour.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, destination);
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", destination='" + destination + '\'' +
                ", duration=" + duration +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", maxGroupSize=" + maxGroupSize +
                ", guide='" + guide + '\'' +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}