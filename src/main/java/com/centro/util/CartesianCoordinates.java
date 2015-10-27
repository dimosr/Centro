package com.centro.util;


public class CartesianCoordinates {
	
double x;
double y;
double z;

	public CartesianCoordinates() {
				
		
	}
	
 public CartesianCoordinates(double xCartesian, double yCartesian, double zCartesian) {
		x = xCartesian;
		y = yCartesian;
		z = zCartesian;
		
	}
 
 public double getX()
 {
	 return this.x;
 }
 public double getY()
 {
	 return this.y;
 }
 public double getZ()
 {
	 return this.z;
 }
}

