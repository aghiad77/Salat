package com.example.salat.models;

public class lastLocation {

    int id;
    double lat;
    double longt;

    public lastLocation(){}

    public lastLocation(int id, double lat, double longt) {
        this.id = id;
        this.lat = lat;
        this.longt = longt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLongt() {
        return longt;
    }
}
