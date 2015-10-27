package com.centro.util.algo;

import com.centro.util.GeoCoordinate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;

public class MiddlePointCalculatorTest {
    
    private static final double DELTA = 1e-15;
    private static MiddlePointCalculator calculator;
    
    @BeforeClass
    public static void setUpDependencies() {
        calculator = new MiddlePointCalculator();
    }
    
    public void getMidPoint() {
        List<GeoCoordinate> locations = new ArrayList<GeoCoordinate>();
        
        locations.add(new GeoCoordinate(51.505217, -0.256007));
        locations.add(new GeoCoordinate(51.498800, -0.174877));
        locations.add(new GeoCoordinate(51.496095, -0.211094));
        
        GeoCoordinate middlePoint = calculator.getMidPoint(locations);
        
        assertEquals(51.500042, middlePoint.getLatitude(), DELTA);
        assertEquals(-0.213991, middlePoint.getLongitude(), DELTA);
    }
}
