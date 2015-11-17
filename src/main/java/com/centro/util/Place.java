package com.centro.util;

import java.util.List;

public class Place {
    
    private final String googleID;
    private final GeoCoordinate location;
    private final String name;
    private PlaceInfo info;
    private List<Long> secondsToReach;
    
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
    
    public void setSecondsToReach(List<Long> seconds) {
        this.secondsToReach = seconds;
    }
    
    public List<Long> getSecondsToReach() {
        return this.secondsToReach;
    }
}
