package com.centro.controllers;

import com.centro.services.HttpService;
import com.centro.util.GeoCoordinate;
import com.centro.util.Place;
import com.centro.util.algo.MeetingPointCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {
    
    private static final String apiVersion = "1.1";
    private static final int TOP_PLACES = 5;
    
    @Autowired
    HttpService httpService;
    
    @Autowired
    private MeetingPointCalculator calculator;
    
    @RequestMapping(value = "/api", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody String index() {
        return new String("{ apiVersion : " + apiVersion + "}");
    }
    
    @RequestMapping(value = "/api/central", method = RequestMethod.POST, produces="application/json", consumes="application/json")
    public @ResponseBody String central(@RequestBody String input) throws JsonProcessingException, IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        
        List<GeoCoordinate> locations = jsonMapper.readValue(input, new TypeReference<List<GeoCoordinate>>(){});
        
        GeoCoordinate centralPoint = calculator.getMeetingPoint(locations);
        String output = jsonMapper.writeValueAsString(centralPoint);
        return output;
    }
    
    @RequestMapping(value = "/api/places", method = RequestMethod.POST, produces="application/json", consumes="application/json") 
    public @ResponseBody String places(@RequestBody String input) throws JsonProcessingException, IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        
        JsonNode requestTree = jsonMapper.readTree(input);
        double latitude = requestTree.findValue("latitude").asDouble();
        double longitude = requestTree.findValue("longitude").asDouble();
        String type;
        if(requestTree.findValue("type") != null) {
            type = requestTree.findValue("type").asText();
        }
        else
            type = "";
        
        List<Place> places = httpService.getPlacesInsideRadius(new GeoCoordinate(latitude, longitude), type);
        
        Iterator<JsonNode> placesNodes = requestTree.get("startingPoints").elements();
        List<GeoCoordinate> startingPoints = new ArrayList();
        List<String> preferredModes = new ArrayList();
        while(placesNodes.hasNext()) {
            JsonNode placeNode = placesNodes.next();
            latitude = placeNode.findValue("latitude").asDouble();
            longitude = placeNode.findValue("longitude").asDouble();
            String mode = placeNode.findValue("mode").asText();
            
            startingPoints.add(new GeoCoordinate(latitude, longitude));
            preferredModes.add(mode);
        }
        
        List<Place> nearestPlaces = httpService.keepNearestPlaces(places, startingPoints, preferredModes, TOP_PLACES);
        String output = jsonMapper.writeValueAsString(nearestPlaces);
        return output;
    }
}
