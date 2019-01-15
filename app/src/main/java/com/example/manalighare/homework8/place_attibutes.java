package com.example.manalighare.homework8;

public class place_attibutes {
    String latitude;
    String longitude;
    String vicinity;
    String rating;
    String name_of_place;
    String type_of_place;

    public place_attibutes() {
    }

    @Override
    public String toString() {
        return "place_attibutes{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", rating='" + rating + '\'' +
                ", name_of_place='" + name_of_place + '\'' +
                ", type_of_place='" + type_of_place + '\'' +
                '}';
    }
}
