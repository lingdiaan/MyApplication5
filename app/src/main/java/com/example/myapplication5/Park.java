package com.example.myapplication5;

public class Park {
//    private String money;
    private String name;
    private int space_num;
    private double lat;
    private double lon;

    public Park(String name, int space_num, double lat, double lon) {
        this.name = name;
        this.space_num = space_num;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpace_num() {
        return space_num;
    }

    public void setSpace_num(int space_num) {
        this.space_num = space_num;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Park{" +
                "name='" + name + '\'' +
                ", space_num=" + space_num +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
