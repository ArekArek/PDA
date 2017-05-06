package com.arek314.pda.api;

import com.arek314.pda.db.model.Information;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InformationRepresentation implements DbMappable<Information> {
    private int id;
    private String mapURL;

    public InformationRepresentation() {
    }

    public InformationRepresentation(Information information) {
        if (information != null) {
            this.id = information.getId();
            this.mapURL = information.getMapURL();
        }
    }

    public InformationRepresentation(@JsonProperty("id") int id, @JsonProperty("mapURL") String mapURL) {
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

        InformationRepresentation that = (InformationRepresentation) o;

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
        return "InformationRepresentation{" +
                "id=" + id +
                ", mapURL='" + mapURL + '\'' +
                '}';
    }

    @Override
    public Information map() {
        return new Information(id, mapURL);
    }
}
