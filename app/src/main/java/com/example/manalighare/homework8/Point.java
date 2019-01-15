package com.example.manalighare.homework8;

public class Point {
    String latitude;
    String longitude;

    public Point(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point() {
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
