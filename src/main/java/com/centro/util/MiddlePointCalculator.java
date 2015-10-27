package com.centro.util;

import java.util.ArrayList;
import java.util.List;

public class MiddlePointCalculator {

	public static void main(String[] args) {
	//this is just a test
	/*List<Coordinates> testList = new ArrayList<Coordinates>();
	testList.add(new Coordinates(51.505217, -0.256007));
	testList.add(new Coordinates(51.498800, -0.174877));
	testList.add(new Coordinates(51.496095, -0.211094));	
	Coordinates midPoint = new Coordinates();
	midPoint = midPointCalculator(testList);
	System.out.println("Midpoint Latitude is: " +midPoint.getLat() +"\n Midpoint Longitude is: "+ midPoint.getLon());
 	*/	
	}

	public static Coordinates midPointCalculator(List<Coordinates> AddressesList){
		Coordinates midPoint = new Coordinates();
		List<CartesianCoordinates> cartesianlist= new ArrayList<CartesianCoordinates>();
		//converting each address to cartesian and add it to the list of cartesian coordinates
		for (int i=0; i<AddressesList.size(); i++)
		{
		double lat=Math.toRadians(AddressesList.get(i).getLat());//toRadians: *(Math.PI)/180;
		double lon=Math.toRadians(AddressesList.get(i).getLon());//*(Math.PI)/180;
		
		double xCartesian= Math.cos(lat)* Math.cos(lon) ;
		double yCartesian= Math.cos(lat)* Math.sin(lon);
		double zCartesian= Math.sin(lat);
		
		
		cartesianlist.add(new CartesianCoordinates (xCartesian, yCartesian, zCartesian));
		double sumx=0;
		double sumy=0;
		double sumz=0;
		 for (int c=0; c < cartesianlist.size(); c++)
		 {
			 sumx += cartesianlist.get(c).getX();
			 sumy += cartesianlist.get(c).getY();
			 sumz += cartesianlist.get(c).getZ();
		 }
		 //calculate midpoint in cartesian coordinates
		 double midPointX = sumx/cartesianlist.size();
		 double midPointY = sumy/cartesianlist.size();
		 double midPointZ = sumz/cartesianlist.size();
		 
		 //convert midpoint from cartesian to geo-coordinates
		 double midPointLonTemp= Math.atan2(midPointY, midPointX);
		 double hype =Math.sqrt((midPointX * midPointX)+(midPointY * midPointY));
		 double midPointLatTemp= Math.atan2(midPointZ, hype);
		 
		 double midPointLat= midPointLatTemp * 180/ Math.PI;
		 double midPointLon= midPointLonTemp *180/ Math.PI;
		 
		 midPoint= new Coordinates(midPointLat, midPointLon);
	
		
		}
		
		return midPoint;
				
	}

}
