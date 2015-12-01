package com.centro.services;

import com.centro.util.GeoCoordinate;
import com.centro.util.Place;
import com.centro.util.PlaceInfo;
import com.centro.util.TransportationMode;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class HttpServiceTest {
    
    @Spy
    @InjectMocks
    private static HttpService service;
    
    @Mock
    private static RestTemplate restRequest;
    
    private static final double DELTA = 1e-15;
    
    @Test
    public void getPlaceGeocodeTest() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [{\"geometry\" : {\"location\" : {\"lat\" : 51.5177367,\"lng\" : -0.1731784}}}],\"status\" : \"OK\"}";
        when(restRequest.getForObject(service.GEOCODE_API, String.class, "imperialcollegelondon")).thenReturn(googleGeocodeResponse);
        
        GeoCoordinate actualCoordinate = service.getPlaceGeocode(new String("imperialcollegelondon"));
        GeoCoordinate expectedCoordinate = new GeoCoordinate(51.5177367, -0.1731784);
        assertEquals(expectedCoordinate.getLatitude(), actualCoordinate.getLatitude(), DELTA);
        assertEquals(expectedCoordinate.getLongitude(), actualCoordinate.getLongitude(), DELTA);
    }
    
    @Test(expected=InvalidResponseException.class)
    public void getPlaceGeoCodeTestException() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [],\"status\" : \"INVALID_REQUEST\"}";
        when(restRequest.getForObject(service.GEOCODE_API, String.class, "imperialcollegelondon")).thenReturn(googleGeocodeResponse);
        
        GeoCoordinate actualCoordinate = service.getPlaceGeocode(new String("imperialcollegelondon"));
    }
    
    @Test
    public void getAddressNameTest() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [{\"formatted_address\" : \"S Wharf Rd, London W2 1PG, UK\", \"place_id\" : \"ChIJdd4hrwug2EcRmSrV3Vo6llI\"}],\"status\" : \"OK\"}";
        GeoCoordinate searchCoordinate = new GeoCoordinate(51.5177367, 51.5177367);
        when(restRequest.getForObject(service.REVERSE_GEOCODE_API, String.class, searchCoordinate.getLatitude(), searchCoordinate.getLongitude())).thenReturn(googleGeocodeResponse);
        
        String actualAddressName = service.getAddressName(searchCoordinate);
        String expectedAddressName = "S Wharf Rd, London W2 1PG, UK";
        assertEquals(expectedAddressName, actualAddressName);
    }
    
    @Test(expected=InvalidResponseException.class)
    public void getAddressNameTestException() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [],\"status\" : \"INVALID_REQUEST\"}";
        GeoCoordinate searchCoordinate = new GeoCoordinate(51.5177367, 51.5177367);
        when(restRequest.getForObject(service.REVERSE_GEOCODE_API, String.class, searchCoordinate.getLatitude(), searchCoordinate.getLongitude())).thenReturn(googleGeocodeResponse);
        
        String actualAddressName = service.getAddressName(searchCoordinate);
    }
    
    @Test
    public void distanceInSecondsTest() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"rows\" : [{\"elements\" : [\n" +
                                        "            {\"distance\" : {\"text\" : \"90.2 km\",\"value\" : 90224},\"duration\" : {\"text\" : \"1 hour 14 mins\",\"value\" : 4416},\"status\" : \"OK\"},\n" +
                                        "            {\"distance\" : {\"text\" : \"3.4 km\",\"value\" : 3436},\"duration\" : {\"text\" : \"12 mins\",\"value\" : 747},\"status\" : \"OK\"}]\n" +
                                        "      }],\"status\" : \"OK\"}";
        GeoCoordinate origin = new GeoCoordinate(51.5177367, -0.1731783999999834);
        List<GeoCoordinate> destinations = new ArrayList();
        destinations.add(new GeoCoordinate(51.75663409999999, -1.2547036999999364));
        destinations.add(new GeoCoordinate(51.5229378, -0.13082059999999274));
        String originParam = String.valueOf(origin.getLatitude()) + "," + String.valueOf(origin.getLongitude());
        String destinationsParam = String.valueOf(destinations.get(0).getLatitude()) + "," +  String.valueOf(destinations.get(0).getLongitude()) + "|" + String.valueOf(destinations.get(1).getLatitude()) + "," +  String.valueOf(destinations.get(1).getLongitude());
        when(restRequest.getForObject(service.DISTANCE_API, String.class, originParam, destinationsParam, TransportationMode.CAR.getMapsFormat(), service.googleApiKey)).thenReturn(googleGeocodeResponse);
        
        List<Long> expectedDistances = Arrays.asList(new Long(4416), new Long(747));
        List<Long> actualDistances = service.distanceInSeconds(origin, destinations);
        assertEquals(expectedDistances.get(0), actualDistances.get(0), DELTA);
        assertEquals(expectedDistances.get(1), actualDistances.get(1), DELTA);
    }
    
    @Test(expected=InvalidResponseException.class)
    public void distanceInSecondsTestException() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [],\"status\" : \"INVALID_REQUEST\"}";
        GeoCoordinate origin = new GeoCoordinate(51.5177367, -0.1731783999999834);
        List<GeoCoordinate> destinations = new ArrayList();
        destinations.add(new GeoCoordinate(51.75663409999999, -1.2547036999999364));
        destinations.add(new GeoCoordinate(51.5229378, -0.13082059999999274));
        String originParam = String.valueOf(origin.getLatitude()) + "," + String.valueOf(origin.getLongitude());
        String destinationsParam = String.valueOf(destinations.get(0).getLatitude()) + "," +  String.valueOf(destinations.get(0).getLongitude()) + "|" + String.valueOf(destinations.get(1).getLatitude()) + "," +  String.valueOf(destinations.get(1).getLongitude());
        when(restRequest.getForObject(service.DISTANCE_API, String.class, originParam, destinationsParam, TransportationMode.CAR.getMapsFormat(), service.googleApiKey)).thenReturn(googleGeocodeResponse);
        
        List<Long> expectedDistances = Arrays.asList(new Long(4416), new Long(747));
        List<Long> actualDistances = service.distanceInSeconds(origin, destinations);
    }
    
    @Test
    public void getPlaceInfo() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"result\" : {\"website\" : \"http://www.test.com\",\n" +
                                        "              \"photos\" : [{\"height\" : 1280,\"photo_reference\" : \"photo_reference1\"},\n" +
                                        "                           {\"height\" : 1280,\"photo_reference\" : \"photo_reference2\"}]},\"status\" : \"OK\"}";
        List<String> imageLinks = Arrays.asList(MessageFormat.format(service.PLACES_PHOTOS_API, "photo_reference1", service.googleApiKey), MessageFormat.format(service.PLACES_PHOTOS_API, "photo_reference2", service.googleApiKey));
        when(restRequest.getForObject(service.PLACES_DETAILS_API, String.class, "testGoogleID1234", service.googleApiKey, "")).thenReturn(googleGeocodeResponse);
        
        
        Place place = new Place("testGoogleID1234", new GeoCoordinate(1234, -0.1234), "testPlaceName");
        PlaceInfo actualInfo = service.getPlaceInfo(place);
        PlaceInfo expectedInfo = new PlaceInfo(imageLinks, "-", "http://www.test.com");
        assertEquals(expectedInfo.averageRating, actualInfo.averageRating);
        assertEquals(expectedInfo.imageLinks.size(), actualInfo.imageLinks.size());
        assertEquals(expectedInfo.imageLinks.get(0), actualInfo.imageLinks.get(0));
        assertEquals(expectedInfo.imageLinks.get(1), actualInfo.imageLinks.get(1));
        assertEquals(expectedInfo.websiteLink, actualInfo.websiteLink);
    }
    
    @Test(expected=InvalidResponseException.class)
    public void getPlaceInfoTestException() throws IOException, InvalidResponseException {
        String googleGeocodeResponse = "{\"results\" : [],\"status\" : \"INVALID_REQUEST\"}";
        List<String> imageLinks = Arrays.asList(MessageFormat.format(service.PLACES_PHOTOS_API, "photo_reference1", service.googleApiKey), MessageFormat.format(service.PLACES_PHOTOS_API, "photo_reference2", service.googleApiKey));
        when(restRequest.getForObject(service.PLACES_DETAILS_API, String.class, "testGoogleID1234", service.googleApiKey, "")).thenReturn(googleGeocodeResponse);
        
        
        Place place = new Place("testGoogleID1234", new GeoCoordinate(1234, -0.1234), "testPlaceName");
        PlaceInfo actualInfo = service.getPlaceInfo(place);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void sortByTimeSumTest() {
        Place place1 = new Place("googleID1", new GeoCoordinate(45.34, -0.1234), "cafe1");
        Place place2 = new Place("googleID2", new GeoCoordinate(54.45, -0.2345), "cafe2");
        Place place3 = new Place("googleID3", new GeoCoordinate(48.93, -0.9911), "cafe3");
        
        place1.setSecondsToReach(Arrays.asList(new Long(233), new Long(344)));      //total cost: 577
        place2.setSecondsToReach(Arrays.asList(new Long(278), new Long(324)));      //total cost: 602
        place3.setSecondsToReach(Arrays.asList(new Long(221), new Long(352)));      //total cost: 573
        
        List<Place> initialPlaceList = Arrays.asList(place1, place2, place3);
        List<Place> expectedSortedList = Arrays.asList(place3, place1, place2);
        
        service.sortByTimeSum(initialPlaceList);
        assertEquals(expectedSortedList, initialPlaceList);
        
        Place place4 = new Place("googleID", new GeoCoordinate(52.93, -0.6911), "cafe4");
        place3.setSecondsToReach(Arrays.asList(new Long(221)));
        List<Place> falsePlaceList = Arrays.asList(place1, place2, place4);
        
        service.sortByTimeSum(falsePlaceList);
    }
    
    @Test
    public void keepNearestPlaces() throws IOException, InvalidResponseException {
        GeoCoordinate startingPoint1 = new GeoCoordinate(56.02, 0.094);
        GeoCoordinate startingPoint2 = new GeoCoordinate(51.54, -0.744);
        List<GeoCoordinate> startingPoints = Arrays.asList(startingPoint1, startingPoint2);
        List<Long> maxTimes = Arrays.asList(Long.MAX_VALUE, Long.MAX_VALUE);
        Place place1 = new Place("googleID1", new GeoCoordinate(45.34, -0.1234), "cafe1");      //233 + 344 from starting points = 577 total
        Place place2 = new Place("googleID2", new GeoCoordinate(54.45, -0.2345), "cafe2");      //278 + 324 from starting points = 602 total
        Place place3 = new Place("googleID3", new GeoCoordinate(48.93, -0.9911), "cafe3");      //221 + 352 from starting points = 573 total
        
        List<Place> initialPlaceList = Arrays.asList(place1, place2, place3);
        List<GeoCoordinate> placesCoords = new ArrayList();
        for(Place place : initialPlaceList)
            placesCoords.add(place.getLocation());
        List<Place> top2List = Arrays.asList(place3, place1);
        
        doReturn(Arrays.asList(new Long(233), new Long(278), new Long(221))).when(service).distanceInSecondsByMode(startingPoint1, placesCoords, TransportationMode.CAR);
        doReturn(Arrays.asList(new Long(344), new Long(324), new Long(352))).when(service).distanceInSecondsByMode(startingPoint2, placesCoords, TransportationMode.CAR);   

        assertEquals(top2List, service.keepNearestPlaces(initialPlaceList, startingPoints, Arrays.asList(TransportationMode.CAR.getMapsFormat(), TransportationMode.CAR.getMapsFormat()), 2, maxTimes));
        
    }
    
    public void filterByMaxTimeTest() {
        Place place1 = new Place("googleID1", new GeoCoordinate(45.34, -0.1234), "cafe1");   
        place1.setSecondsToReach(Arrays.asList(new Long(550), new Long(480)));
        Place place2 = new Place("googleID2", new GeoCoordinate(54.45, -0.2345), "cafe2");
        place2.setSecondsToReach(Arrays.asList(new Long(670), new Long(530)));
        List<Place> unfilteredPlaces = Arrays.asList(place1, place2);
        
        List<Long> maxTimes = Arrays.asList(new Long(580), new Long(1200));
        List<Place> filteredPlaces = service.filterByMaxTime(unfilteredPlaces, maxTimes);
        
        assertEquals(1, filteredPlaces.size());
        assertEquals(550, filteredPlaces.get(0).getSecondsToReach().get(0).doubleValue(), DELTA);
        assertEquals(480, filteredPlaces.get(0).getSecondsToReach().get(1).doubleValue(), DELTA);
    }
}
