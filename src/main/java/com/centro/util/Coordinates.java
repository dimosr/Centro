package com.centro.util;


public class Coordinates {
	double latitude;
	double longitude;

	public Coordinates() {
		latitude=0;
		longitude=0;
		
	}
	public Coordinates(double lat, double lon)
	{
		latitude=lat;
		longitude=lon;	
	}

	public double getLat()
	{
		return this.latitude;
		
	}
	public double getLon()
	{
		return this.longitude;
		
	}
}
