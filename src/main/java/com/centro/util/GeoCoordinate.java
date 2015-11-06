package com.centro.util;

import static java.lang.Math.PI;
import java.util.ArrayList;
import java.util.List;

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
    
    public double distanceTo(GeoCoordinate destination) {
        int earthRadius = 6371000;
        double phi1 = Math.toRadians(this.getLatitude());
        double phi2 = Math.toRadians(destination.getLatitude());
        double deltaPhi = Math.toRadians((destination.getLatitude()-this.getLatitude()));
        double deltaLambda = Math.toRadians((destination.getLongitude()-this.getLongitude()));
        
        double alpha = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(alpha), Math.sqrt(1-alpha));
        
        double distanceInMeters = earthRadius*c;
        return distanceInMeters;
    }
    
    public List<GeoCoordinate> calculateEquiDistantPoints(double distance, int numberOfPoints) {
        double centerLatitude = this.getLatitude();
        double centerLongitude = this.getLongitude();
        
        List<GeoCoordinate> points = new ArrayList<GeoCoordinate>();
        double pointLatitude, pointLongitude;
        for(int i = 0; i < numberOfPoints; i++) {
            double angle = (2*PI/numberOfPoints)*i;
            pointLatitude = Math.asin(Math.sin(centerLatitude) * Math.cos(distance) + Math.cos(centerLatitude)* Math.sin(distance) * Math.cos(angle));
            pointLongitude = centerLongitude +Math.atan2(Math.sin(angle) * Math.sin(distance) * Math.cos(centerLatitude), Math.cos(distance) - Math.sin(centerLatitude) * Math.sin(pointLatitude));
            if (pointLongitude < -Math.PI)
        	pointLongitude += (2 * Math.PI);
            else if (pointLongitude > Math.PI)
        	pointLongitude -= (2 * Math.PI);
            points.add(new GeoCoordinate(pointLatitude, pointLongitude));
        }
        return points;
    }
    
}
