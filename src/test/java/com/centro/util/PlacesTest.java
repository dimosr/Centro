package com.centro.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class PlacesTest {
    
    @Test
    public void PlaceTypeTest() {
        PlaceType placeType = PlaceType.HEALTH;
        
        assertEquals("health", placeType.getGoogleApiName());
        assertEquals("Hospital", placeType.getFrontEndName());
    }
}
