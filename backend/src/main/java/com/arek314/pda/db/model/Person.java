package com.arek314.pda.db.model;

public class Person {
    private int id;
    private double latitude;
    private double longitude;
    private boolean isOnline;
    private String label;

    public Person() {
    }

    public Person(int id, double latitude, double longitude, boolean isOnline, String label) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != person.id) return false;
        if (Double.compare(person.latitude, latitude) != 0) return false;
        if (Double.compare(person.longitude, longitude) != 0) return false;
        if (isOnline != person.isOnline) return false;
        return label.equals(person.label);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isOnline ? 1 : 0);
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isOnline=" + isOnline +
                ", label='" + label + '\'' +
                '}';
    }
}
