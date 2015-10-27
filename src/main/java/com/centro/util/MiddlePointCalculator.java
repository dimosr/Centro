package com.centro.util;

import java.util.ArrayList;
import java.util.List;

public class MiddlePointCalculator {

	public static GeoCoordinate getMidPoint(List<GeoCoordinate> locations){
            GeoCoordinate midPoint = null ;
            double sumX=0, sumY=0, sumZ=0;
            for (GeoCoordinate location : locations) {
                CartesianCoordinate cartesianLocation = fromGeoToCartesian(location);
                sumX += cartesianLocation.getX();
                sumY += cartesianLocation.getY();
                sumZ += cartesianLocation.getZ();      
            }
            double midPointX = sumX/locations.size();
            double midPointY = sumY/locations.size();
            double midPointZ = sumZ/locations.size();
            
            midPoint = fromCartesianToGeo(new CartesianCoordinate(midPointX, midPointY, midPointZ));
		
            return midPoint;		
	}
        
        public static CartesianCoordinate fromGeoToCartesian(GeoCoordinate geoCoordinate) {
            double latitudeRadians = Math.toRadians(geoCoordinate.getLatitude());
            double longitudeRadians = Math.toRadians(geoCoordinate.getLongitude());
            
            double xCartesian= Math.cos(latitudeRadians)* Math.cos(longitudeRadians) ;
            double yCartesian= Math.cos(latitudeRadians)* Math.sin(longitudeRadians);
            double zCartesian= Math.sin(latitudeRadians);
            
            CartesianCoordinate cartesianCoord = new CartesianCoordinate(xCartesian, yCartesian, zCartesian);
            return cartesianCoord;
        }
        
        public static GeoCoordinate fromCartesianToGeo(CartesianCoordinate cartesianCoordinate) {
            double longitudeTemp = Math.atan2(cartesianCoordinate.getY(), cartesianCoordinate.getX());
            double hype = Math.sqrt((cartesianCoordinate.getX() * cartesianCoordinate.getX())+(cartesianCoordinate.getY() * cartesianCoordinate.getY()));
            double latitudeTemp = Math.atan2(cartesianCoordinate.getZ(), hype);
            
            double longitude= longitudeTemp *180/ Math.PI;
            double latitude= latitudeTemp * 180/ Math.PI;
            
            GeoCoordinate geoCoordinate = new GeoCoordinate(latitude, longitude);
            return geoCoordinate;
        }

}
