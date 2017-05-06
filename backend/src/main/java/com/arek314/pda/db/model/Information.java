package com.arek314.pda.db.model;

public class Information {
    private int id;
    private String mapURL;

    public Information() {
    }

    public Information(String mapURL) {
        this.mapURL = mapURL;
    }

    public Information(int id, String mapURL) {
        this.id = id;
        this.mapURL = mapURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMapURL() {
        return mapURL;
    }

    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Information that = (Information) o;

        if (id != that.id) return false;
        return mapURL != null ? mapURL.equals(that.mapURL) : that.mapURL == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Information{" +
                "id=" + id +
                ", mapURL='" + mapURL + '\'' +
                '}';
    }
}
