package com.centro.controllers;

import com.centro.util.GeoCoordinate;
import com.centro.util.algo.MeetingPointCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {
    
    private static final String apiVersion = "1.0";
    
    @Autowired
    @Qualifier("equiDistantCalculator")
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
    
}
