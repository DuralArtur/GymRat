package com.example.android.gymrat.objects;

/**
 * Created by Artur on 21-Oct-16.
 */

public class Gym {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Gym(String name, String address, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getName(){return name;}
    public String getAddress(){return address;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
}
