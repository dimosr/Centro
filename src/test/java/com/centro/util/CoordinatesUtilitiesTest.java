package com.centro.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class CoordinatesUtilitiesTest {
    
    private static final double metersDelta = 100;
    
    @Test
    public void testCoordinatesDistance() {
        GeoCoordinate origin = new GeoCoordinate(51.49818, -0.17820499999993444);
        GeoCoordinate destination = new GeoCoordinate(51.514408, -0.11737700000003315);
        
        double distance = origin.distanceTo(destination);
        
        assertEquals(4581.7, distance, metersDelta);
    }
}
