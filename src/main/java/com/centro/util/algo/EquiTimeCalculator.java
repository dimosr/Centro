package com.centro.util.algo;

import com.centro.util.GeoCoordinate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("equiTimeCalculator")
public class EquiTimeCalculator implements MeetingPointCalculator {
    
    @Autowired
    @Qualifier("equiDistantCalculator")
    private MeetingPointCalculator equiDistantCalculator;
    
    public GeoCoordinate getMidPoint(List<GeoCoordinate> locations) {
        GeoCoordinate equiDistantPoint = equiDistantCalculator.getMeetingPoint(locations);
        
        return equiDistantPoint;
    }
    
    public GeoCoordinate getMeetingPoint(List<GeoCoordinate> locations) {
        return getMidPoint(locations);
    }
}
