package com.centro.util;

public class CartesianCoordinate {
	
    private double x;
    private double y;
    private double z;
	
    public CartesianCoordinate(double xCartesian, double yCartesian, double zCartesian) {
        x = xCartesian;
        y = yCartesian;
        z = zCartesian;
    }
 
    public double getX() {
	return this.x;
    }
    
    public double getY() {
	return this.y;
    }
 
    public double getZ() {
	return this.z;
    }
}

