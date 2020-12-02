package com.example.myapplication5;

public class Order {
    private String parkName;
    private String parkTime;
    private String parkId;

    public Order(String parkName, String parkTime) {
        this.parkName = parkName;
        this.parkTime = parkTime;
//        this.parkId = parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getParkTime() {
        return parkTime;
    }

    public void setParkTime(String parkTime) {
        this.parkTime = parkTime;
    }
}
