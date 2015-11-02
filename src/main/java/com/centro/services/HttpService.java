package com.centro.services;

import com.centro.util.GeoCoordinate;
import com.centro.util.TransportationMode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    
    private static final String GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?address={address}";
    private static final String REVERSE_GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?latlng={latitude},{longitude}";
    private static final String DISTANCE_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&mode={mode}";
    
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
    
    public long DistanceInSecondsByMode(GeoCoordinate from, GeoCoordinate to, TransportationMode mode) throws IOException {
        String origin = from.getLatitude() + "," + from.getLongitude();
        String destination = to.getLatitude() + "," + to.getLongitude();
        
        String response = restRequest.getForObject(DISTANCE_API, String.class, origin, destination, mode.getMapsFormat());
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode distance = jsonMapper.readTree(response);
        distance = distance.get("rows").findValue("elements").findValue("duration");
        long seconds = distance.findValue("value").asLong();
        
        return seconds;
    }
    
    public long DistanceInSecondsByMode(String from, String to, TransportationMode mode) throws IOException {
        GeoCoordinate source = getPlaceGeocode(from);
        GeoCoordinate destination = getPlaceGeocode(to);
        return DistanceInSecondsByMode(source, destination, mode);
    }
    
    public long DistanceInSeconds(GeoCoordinate from, GeoCoordinate to) throws IOException {
        return DistanceInSecondsByMode(from, to, DEFAULT_MODE);
    }
    
    public long DistanceInSeconds(String from, String to) throws IOException {
        return DistanceInSecondsByMode(from, to, DEFAULT_MODE);
    }
}
