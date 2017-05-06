package com.arek314.pda.Map;

public class Person {
    private int id;
    private double latitude;
    private double longitude;
    private String label;

    public Person(int id, double latitude, double longitude, String label) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLabel() {
        return label;
    }
}
