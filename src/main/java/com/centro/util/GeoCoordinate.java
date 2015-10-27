package com.centro.util;

public class GeoCoordinate {
	
    private double latitude;
    private double longitude;
        
    public GeoCoordinate(double lat, double lon) {
        latitude=lat;
        longitude=lon;	
    }
        
    public GeoCoordinate() {
        this(0, 0);	
    }

    public double getLatitude() {
        return this.latitude;	
    }
    
    public double getLongitude() {
        return this.longitude;
    }
}
