package com.kdao.cmpe235_project.data;

public class Location {

    private double latitude;
    private double longitude;
    private String address;
    private String name;

    public Location(double latitude, double longitude, String address, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.name = name;
    }
}
