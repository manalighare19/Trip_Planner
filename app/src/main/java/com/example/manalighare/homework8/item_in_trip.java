package com.example.manalighare.homework8;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class item_in_trip implements Serializable {

    String user_name;
    String trip_id;
    String travel_destination;
    String travel_date;
    String trip_Name;
    ArrayList<places_to_visit> places_to_visit=new ArrayList<>();


    public item_in_trip() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTravel_destination() {
        return travel_destination;
    }

    public void setTravel_destination(String travel_destination) {
        this.travel_destination = travel_destination;
    }

    public String getTravel_date() {
        return travel_date;
    }

    public void setTravel_date(String travel_date) {
        this.travel_date = travel_date;
    }

    public String getTrip_Name() {
        return trip_Name;
    }

    public void setTrip_Name(String trip_Name) {
        this.trip_Name = trip_Name;
    }

    public ArrayList<com.example.manalighare.homework8.places_to_visit> getPlaces_to_visit() {
        return places_to_visit;
    }

    public void setPlaces_to_visit(ArrayList<com.example.manalighare.homework8.places_to_visit> places_to_visit) {
        this.places_to_visit = places_to_visit;
    }

    @Override
    public String toString() {
        return "item_in_trip{" +
                "user_name='" + user_name + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", travel_destination='" + travel_destination + '\'' +
                ", travel_date='" + travel_date + '\'' +
                ", trip_Name='" + trip_Name + '\'' +
                ", places_to_visit=" + places_to_visit +
                '}';
    }
}
