package com.centro.util;

public class CoordinatesConverter {
    
    public CartesianCoordinate fromGeoToCartesian(GeoCoordinate geoCoordinate) {
        double latitudeRadians = Math.toRadians(geoCoordinate.getLatitude());
        double longitudeRadians = Math.toRadians(geoCoordinate.getLongitude());
            
        double xCartesian= Math.cos(latitudeRadians)* Math.cos(longitudeRadians) ;
        double yCartesian= Math.cos(latitudeRadians)* Math.sin(longitudeRadians);
        double zCartesian= Math.sin(latitudeRadians);
            
        CartesianCoordinate cartesianCoord = new CartesianCoordinate(xCartesian, yCartesian, zCartesian);
        return cartesianCoord;
    }
        
    public GeoCoordinate fromCartesianToGeo(CartesianCoordinate cartesianCoordinate) {
        double longitudeTemp = Math.atan2(cartesianCoordinate.getY(), cartesianCoordinate.getX());
        double hype = Math.sqrt((cartesianCoordinate.getX() * cartesianCoordinate.getX())+(cartesianCoordinate.getY() * cartesianCoordinate.getY()));
        double latitudeTemp = Math.atan2(cartesianCoordinate.getZ(), hype);
            
        double longitude= longitudeTemp *180/ Math.PI;
        double latitude= latitudeTemp * 180/ Math.PI;
            
        GeoCoordinate geoCoordinate = new GeoCoordinate(latitude, longitude);
        return geoCoordinate;
    }
}
