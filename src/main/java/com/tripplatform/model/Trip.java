package com.tripplatform.model;

public class Trip {
    private int id;
    private String name;
    private String destination;

    public Trip() {}

    public Trip(int id, String name, String destination) {
        this.id = id;
        this.name = name;
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}