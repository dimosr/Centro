package com.centro.services;

import com.centro.util.CoordinatesConverter;
import com.centro.util.GeoCoordinate;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.web.client.RestTemplate;


public class HttpServiceTest {
    
    private static HttpService service;
    private static RestTemplate restRequest;
    private static final double DELTA = 1e-15;
    
    @BeforeClass
    public static void setUpDependencies() {
        restRequest = mock(RestTemplate.class);
        service = new HttpService(restRequest);
    }
    
    @Test
    public void getPlaceGeocodeTest() throws IOException {
        String googleGeocodeResponse = "{\"results\" : [{\"geometry\" : {\"location\" : {\"lat\" : 51.5177367,\"lng\" : -0.1731784}}}],\"status\" : \"OK\"}";
        when(restRequest.getForObject(service.GEOCODE_API, String.class, "imperialcollegelondon")).thenReturn(googleGeocodeResponse);
        
        GeoCoordinate actualCoordinate = service.getPlaceGeocode(new String("imperialcollegelondon"));
        GeoCoordinate expectedCoordinate = new GeoCoordinate(51.5177367, -0.1731784);
        assertEquals(expectedCoordinate.getLatitude(), actualCoordinate.getLatitude(), DELTA);
        assertEquals(expectedCoordinate.getLongitude(), actualCoordinate.getLongitude(), DELTA);
    }
}
