package com.epam.trip.entity;

import java.util.Objects;

public class Place extends BaseEntity {
    private String name;
    private String city;
    private String country;
    private String description;
    private String category;
    private double rating;
    private double entryFee;

    public Place() {
        super();
    }

    public Place(Long id, String name, String city, String country,
            String description, String category, double rating, double entryFee) {
        super(id);
        this.name = name;
        this.city = city;
        this.country = country;
        this.description = description;
        this.category = category;
        this.rating = rating;
        this.entryFee = entryFee;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name) &&
                Objects.equals(city, place.city) &&
                Objects.equals(country, place.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, city, country);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", rating=" + rating +
                ", entryFee=" + entryFee +
                '}';
    }
}