package com.centro.util;

public enum TransportationMode {
    CAR("driving"), 
    WALKING("walking"), 
    BICYCLE("bicycling"), 
    PUBLIC_TRANSIT("transit");
    
    private String mapsFormat;
    TransportationMode(String mapsFormat) {
        this.mapsFormat = mapsFormat;
    }
    
    public String getMapsFormat() {
        return mapsFormat;
    }
}
