package com.arek314.pda.api;

import com.arek314.pda.db.model.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRepresentation implements DbMappable<Person> {
    private int id;
    private double latitude;
    private double longitude;
    private boolean isOnline;
    private String label;

    public PersonRepresentation(Person person) {
        this.id = person.getId();
        this.latitude = person.getLatitude();
        this.longitude = person.getLongitude();
        this.isOnline = person.getIsOnline();
        this.label = person.getLabel();
    }

    public PersonRepresentation(@JsonProperty("id") int id, @JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude, @JsonProperty("isOnline") boolean isOnline, @JsonProperty("label") String label) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
        this.label = label;
    }

    @Override
    public Person map() {
        return new Person(id, latitude, longitude, isOnline, label);
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

    public boolean isOnline() {
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
    public String toString() {
        return "PersonRepresentation{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isOnline=" + isOnline +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonRepresentation that = (PersonRepresentation) o;

        if (id != that.id) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (isOnline != that.isOnline) return false;
        return label != null ? label.equals(that.label) : that.label == null;

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
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }
}
