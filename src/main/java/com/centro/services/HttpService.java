package com.centro.services;

import com.centro.util.GeoCoordinate;
import com.centro.util.Place;
import com.centro.util.TransportationMode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    
    private static final String GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?address={address}";
    private static final String REVERSE_GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?latlng={latitude},{longitude}";
    private static final String DISTANCE_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&mode={mode}";
    private static final String PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={location}&radius={radius}&types={types}&key={key}&name={name}";
    
    @Value("${google.place.api.key}")
    private String googleApiKey;
    
    
    private static final TransportationMode DEFAULT_MODE = TransportationMode.CAR;
    
    private RestTemplate restRequest;
    private HttpHeaders headers;
    private HttpStatus status;
    
    public HttpService() {
        restRequest = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }
    
    public GeoCoordinate getPlaceGeocode(String address) throws IOException {
        String response = restRequest.getForObject(GEOCODE_API, String.class, address);
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode location = jsonMapper.readTree(response);
        location = location.get("results").findValue("geometry").findValue("location");
        
        GeoCoordinate coordinate = new GeoCoordinate(location.findValue("lat").asDouble(), location.findValue("lng").asDouble());
        
        return coordinate;
    }
    
    public String getAddressName(GeoCoordinate coordinate) throws IOException {
        String response = restRequest.getForObject(REVERSE_GEOCODE_API, String.class, coordinate.getLatitude(), coordinate.getLongitude());
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode location = jsonMapper.readTree(response);
        location = location.get("results");
        
        String address = location.findValue("formatted_address").textValue();
        return address;
    }
    
    public List<Long> DistanceInSecondsByMode(GeoCoordinate from, List<GeoCoordinate> to, TransportationMode mode) throws IOException {
        String origin = from.getLatitude() + "," + from.getLongitude();
        String destinations = "";
        for(int i = 0; i < to.size(); i++) {
            GeoCoordinate destination = to.get(i);
            destinations += destination.getLatitude() + "," + destination.getLongitude();
            if(i < (to.size()-1))    destinations += "|";
        }
        
        String response = restRequest.getForObject(DISTANCE_API, String.class, origin, destinations, mode.getMapsFormat());
        
        List<Long> seconds = new ArrayList<Long>();
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode distance = jsonMapper.readTree(response);
        List<JsonNode> paths = distance.get("rows").findValue("elements").findValues("duration");
        for(JsonNode path : paths) {
            long currentDist = path.findValue("value").asLong();
            seconds.add(new Long(currentDist));
        }
        
        return seconds;
    }
    
    public List<Long> DistanceInSeconds(GeoCoordinate from, List<GeoCoordinate> to) throws IOException {
        return DistanceInSecondsByMode(from, to, TransportationMode.CAR);
    }
    
    public List<Place> getPlacesInsideRadius(GeoCoordinate center, double radius, String type) throws IOException {
        List<Place> places = new ArrayList<Place>();
        
        String locationString = center.getLatitude() + "," + center.getLongitude();
        String response = restRequest.getForObject(PLACES_API, String.class, locationString, radius, type, googleApiKey, "");
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode responseTree = jsonMapper.readTree(response);
        Iterator<JsonNode> placesNodes = responseTree.get("results").elements();
        
        while(placesNodes.hasNext()) {
            JsonNode place = placesNodes.next();
            double latitude = place.findValue("geometry").findValue("lat").asDouble();
            double longitude = place.findValue("geometry").findValue("lng").asDouble();
            GeoCoordinate location = new GeoCoordinate(latitude, longitude);
            String name = place.findValue("name").asText();
            
            places.add(new Place(location, name));
        }
        places = (places.size() > 10) ? places.subList(0, 11) : places.subList(0, places.size());
        
        return places;
    }
}
