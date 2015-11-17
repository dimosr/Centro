package com.centro.services;

import com.centro.util.GeoCoordinate;
import com.centro.util.Place;
import com.centro.util.PlaceInfo;
import com.centro.util.TransportationMode;
import com.centro.util.Tuple;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    
    public static final String GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?address={address}";
    public static final String REVERSE_GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?latlng={latitude},{longitude}";
    public static final String DISTANCE_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&mode={mode}";
    public static final String PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={location}&radius=1000&types={types}&key={key}&name={name}";
    public static final String PLACES_DETAILS_API = "https://maps.googleapis.com/maps/api/place/details/json?placeid={placeID}&key={key}";
    public static final String PLACES_PHOTOS_API = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference={0}&key={1}";
    
    private static final int PLACES_IMAGES_LIMIT = 4;
    
    @Value("${google.place.api.key}")
    public String googleApiKey;
    
    
    public static final TransportationMode DEFAULT_MODE = TransportationMode.CAR;
    
    private RestTemplate restRequest;
    private HttpHeaders headers;
    private HttpStatus status;
    
    public HttpService() {
        restRequest = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }
    
    public HttpService(RestTemplate restRequest) {
        this();
        this.restRequest = restRequest;
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
    
    public List<Long> distanceInSecondsByMode(GeoCoordinate from, List<GeoCoordinate> to, TransportationMode mode) throws IOException {
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
    
    public List<Long> distanceInSeconds(GeoCoordinate from, List<GeoCoordinate> to) throws IOException {
        return distanceInSecondsByMode(from, to, TransportationMode.CAR);
    }
    
    public List<Place> keepNearestPlaces(List<Place> unfilteredPlaces, List<GeoCoordinate> origins, List<String> modes, int topSize) throws IOException {
        List<GeoCoordinate> placesCoords = new ArrayList();
        for(Place place : unfilteredPlaces)
            placesCoords.add(place.getLocation());
        
        for(int i = 0; i < origins.size(); i++) {
            TransportationMode mode = TransportationMode.findByMapsFormat(modes.get(i));
            GeoCoordinate startingPoint = origins.get(i);
            List<Long> distancesInSeconds = distanceInSecondsByMode(startingPoint, placesCoords, mode);
            for(int j = 0; j < distancesInSeconds.size(); j++)
                unfilteredPlaces.get(j).addSecondToReach(distancesInSeconds.get(j));
        }
        sortByTimeSum(unfilteredPlaces);
        
        int topLimit = (unfilteredPlaces.size() > topSize) ? topSize : unfilteredPlaces.size();
        List<Place> nearestPlaces = unfilteredPlaces.subList(0, topLimit);
        
        return nearestPlaces;
    }
    
    public List<Place> getPlacesInsideRadius(GeoCoordinate center, String type) throws IOException {
        
        String locationString = center.getLatitude() + "," + center.getLongitude();
        String response = restRequest.getForObject(PLACES_API, String.class, locationString, type, googleApiKey, "");
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode responseTree = jsonMapper.readTree(response);
        Iterator<JsonNode> placesNodes = responseTree.get("results").elements();
        
       
        List<Place> selectedPlaces = new ArrayList();
        while(placesNodes.hasNext()) {
            JsonNode placeNode = placesNodes.next();
            String id = placeNode.findValue("place_id").asText();
            double latitude = placeNode.findValue("geometry").findValue("lat").asDouble();
            double longitude = placeNode.findValue("geometry").findValue("lng").asDouble();
            GeoCoordinate location = new GeoCoordinate(latitude, longitude);
            String name = placeNode.findValue("name").asText();
            
            Place selectedPlace = new Place(id, location, name);
            selectedPlace.setInfo(getPlaceInfo(selectedPlace));
            
            selectedPlaces.add(selectedPlace);
        }
        return selectedPlaces;
    }
    
    
    public PlaceInfo getPlaceInfo(Place place) throws IOException {
        String response = restRequest.getForObject(PLACES_DETAILS_API, String.class, place.getGoogleID(), googleApiKey, "");
        
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode responseTree = jsonMapper.readTree(response).findValue("result");
        
        String websiteLink = responseTree.has("website") ? responseTree.findValue("website").asText() : "-";
        
        Iterator<JsonNode> photosNodes = responseTree.has("photos") ? responseTree.get("photos").elements() : null;
        int retrievedImages = 0;
        List<String> imageLinks = new ArrayList();
        if(photosNodes != null) {
            while(photosNodes.hasNext() && (retrievedImages < PLACES_IMAGES_LIMIT)  ) {
                JsonNode photo = photosNodes.next();
                String referenceID = photo.findValue("photo_reference").asText();
                String imageLink = MessageFormat.format(PLACES_PHOTOS_API, referenceID, googleApiKey);
                imageLinks.add(imageLink);
                retrievedImages++;
            }
        }   
        
        String averageRating = responseTree.has("rating") ? responseTree.findValue("rating").asText() : "-";
        PlaceInfo placeInfo = new PlaceInfo(imageLinks, averageRating, websiteLink);
        
        return placeInfo;
    }
    
    public void sortByTimeSum(List<Place> unsortedPlaces) {
        for(int i = 0; i < (unsortedPlaces.size()-1); i++) {
            if(unsortedPlaces.get(i).getSecondsToReach().size() != unsortedPlaces.get(i+1).getSecondsToReach().size())
                throw new IllegalArgumentException();
        }
        List<Tuple<Place, Long>> placesWithTime = new ArrayList();
        Collections.sort(unsortedPlaces, new Comparator<Place>() {
            @Override
            public int compare(Place place1, Place place2) {
                long sum1 = 0, sum2 = 0;
                for(int i = 0; i < place1.getSecondsToReach().size(); i++) {
                    sum1 += place1.getSecondsToReach().get(i);
                    sum2 += place2.getSecondsToReach().get(i);
                }
                if(sum1 < sum2)
                    return -1;
                else if(sum1 == sum2)
                    return 0;
                else
                    return 1;
            }
        });
    }
}
