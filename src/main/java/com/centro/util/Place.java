package com.centro.util;

public class Place {
    
    private String googleID;
    private GeoCoordinate location;
    private String name;
    private PlaceInfo info;
    
    public Place(String googleID, GeoCoordinate location, String name) {
        this.googleID = googleID;
        this.location = location;
        this.name = name;
    }
    
    public GeoCoordinate getLocation() {
        return this.location;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getGoogleID() {
        return this.googleID;
    }
    
    public void setInfo(PlaceInfo info) {
        this.info = info;
    }
    
    public PlaceInfo getInfo() {
        return this.info;
    }
}
