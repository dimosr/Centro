package com.centro.util;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

public class CoordinatesConverterTest {
    
    private static final double DELTA = 1e-15;
    private static CoordinatesConverter converter;
    
    @BeforeClass
    public static void setUpDependencies() {
        converter = new CoordinatesConverter();
    }
    
    @Test
    public void testConversionFromGeoToCartesian() {
        GeoCoordinate coordinate = new GeoCoordinate(51.498800, -0.174877);
        
        CartesianCoordinate cartesianCoord = converter.fromGeoToCartesian(coordinate);
        
        assertEquals(0.6225281277231871, cartesianCoord.getX(), DELTA);
        assertEquals(-0.0019000734500125712, cartesianCoord.getY(), DELTA);
        assertEquals(0.7825951187647082, cartesianCoord.getZ(), DELTA);
    }
    
    @Test 
    public void testConversionfromCartesianToGeo() {
        CartesianCoordinate cartesianCoord = new CartesianCoordinate(0.6225281277231871, -0.0019000734500125712, 0.7825951187647082);
        
        GeoCoordinate coordinate = converter.fromCartesianToGeo(cartesianCoord);
        
        assertEquals(51.498800, coordinate.getLatitude(), DELTA);
        assertEquals(-0.174877, coordinate.getLongitude(), DELTA);
    }
}
