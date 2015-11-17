package com.centro.util;

public enum TransportationMode {
    CAR("driving", "Driving"), 
    WALKING("walking", "Walking"), 
    BICYCLE("bicycling", "Cycling"), 
    PUBLIC_TRANSIT("transit", "Public transportation");
    
    private String mapsFormat;
    private String frontEndName;
    TransportationMode(String mapsFormat, String frontEndName) {
        this.mapsFormat = mapsFormat;
        this.frontEndName = frontEndName;
    }
    
    public String getMapsFormat() {
        return mapsFormat;
    }
    
    public String getFrontEndName() {
        return frontEndName;
    }
}
