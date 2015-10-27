package com.centro.util;

public class CartesianCoordinate {
	
    double x;
    double y;
    double z;
	
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

