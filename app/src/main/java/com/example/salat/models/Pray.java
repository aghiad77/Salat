package com.example.salat.models;

public class Pray {
    int id;
    String pray;
    String Date;

    public Pray() {
    }

    public Pray(int id , String pray, String date) {
        this.id = id;
        this.pray = pray;
        Date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPray(String pray) {
        this.pray = pray;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPray() {
        return pray;
    }

    public String getDate() {
        return Date;
    }
}
