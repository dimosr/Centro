package com.centro.controllers;

import com.centro.services.HttpService;
import com.centro.services.InvalidResponseException;
import com.centro.util.GeoCoordinate;
import com.centro.util.Place;
import com.centro.util.PlaceInfo;
import com.centro.util.PlaceType;
import com.centro.util.algo.EquiDistantCalculator;
import com.centro.util.algo.MeetingPointCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"classpath:mvc-dispatcher-servlet.xml"})
@Controller
public class ApiControllerTest {
    
    @InjectMocks
    private ApiController controller;
    
    @Mock
    private static HttpService service;
    
    @Spy
    private MeetingPointCalculator calculator = new EquiDistantCalculator();
    
    
    @Test
    public void centralTest() throws IOException {
        String query = "[{\"latitude\": 51.5073509, \"longitude\": -0.1277583}, {\"latitude\": 51.7520209, \"longitude\": -1.2577263}]";
        String expectedResponse = "{\"latitude\":51.63104157780866,\"longitude\":-0.691218624225265}";
        
        String actualResponse = controller.central(query);
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test
    public void placesTest() throws IOException, InvalidResponseException {
        GeoCoordinate inputCoord = new GeoCoordinate(51.53128283381906, -0.15418765192555384);
        PlaceType placeType = PlaceType.NIGHT_CLUB;
        String query = "{\"latitude\": " + inputCoord.getLatitude() + ", \"longitude\":" + inputCoord.getLongitude() + ", \"type\": \"" + placeType.getGoogleApiName() + "\"," + 
                        "\"startingPoints\":[{\"latitude\": 51.5335578, \"longitude\": -0.15315775, \"mode\": \"" + HttpService.DEFAULT_MODE.getMapsFormat() + "\"}]}";
        
        Place place1 = new Place("googleID1", new GeoCoordinate(51.4977507, -0.0994656), "club1");
        PlaceInfo info1 = new PlaceInfo(Arrays.asList("www.image1.com", "www.image2.com"), "4.2", "http://club1.com");
        place1.setInfo(info1);
        Place place2 = new Place("googleID2", new GeoCoordinate(51.4977507, -0.0994656), "club2");
        PlaceInfo info2 = new PlaceInfo(Arrays.asList("www.image3.com"), "2.3", "http://club2.com");
        place2.setInfo(info2);
        List<Place> places = Arrays.asList(place1, place2);
        List<Place> sortedPlaces = Arrays.asList(place2, place1);
        when(service.getPlacesInsideRadius(any(GeoCoordinate.class), any(String.class))).thenReturn(places);
        when(service.keepNearestPlaces(any(List.class), any(List.class), any(List.class),any(Integer.class), any(List.class))).thenReturn(sortedPlaces);
        
        ObjectMapper jsonMapper = new ObjectMapper();
        String expectedResponse = jsonMapper.writeValueAsString(sortedPlaces);
        
        ResponseEntity<String> actualResponse = controller.places(query);
        assertEquals(expectedResponse, actualResponse.getBody());
        
        doThrow(new InvalidResponseException()).when(service).getPlacesInsideRadius(any(GeoCoordinate.class), any(String.class));
        actualResponse = controller.places(query);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, actualResponse.getStatusCode());
    }
    
}
