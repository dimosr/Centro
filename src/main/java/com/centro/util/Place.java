package com.centro.util;

public class Place {
    
    private GeoCoordinate location;
    private String name;
    
    public Place(GeoCoordinate location, String name) {
        this.location = location;
        this.name = name;
    }
    
    public GeoCoordinate getLocation() {
        return this.location;
    }
    
    public String getName() {
        return this.name;
    }
}
