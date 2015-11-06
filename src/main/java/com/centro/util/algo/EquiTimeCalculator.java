package com.centro.util.algo;

import com.centro.services.HttpService;
import com.centro.util.GeoCoordinate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("equiTimeCalculator")
public class EquiTimeCalculator implements MeetingPointCalculator {
    
    private static final double INITIAL_CIRCLE_SIZE_PERCENTAGE = 0.3;
    private static final int CIRCLE_POINTS_USED = 8;
    
    @Autowired
    HttpService httpService;
    
    @Autowired
    @Qualifier("equiDistantCalculator")
    private MeetingPointCalculator equiDistantCalculator;
    
    public GeoCoordinate getMidPoint(List<GeoCoordinate> locations) throws IOException {
        GeoCoordinate equiDistantPoint = equiDistantCalculator.getMeetingPoint(locations);
        
        GeoCoordinate circleCenter = equiDistantPoint;
        double costInDistance = 0;
        for(GeoCoordinate point : locations) {
            costInDistance += circleCenter.distanceTo(point);
        }
        double initialCircleSize = (costInDistance/locations.size())*INITIAL_CIRCLE_SIZE_PERCENTAGE;
        
        List<Long> timeDistances = httpService.DistanceInSeconds(circleCenter, locations);
        double costInTime = 0;
        for(Long timeDistance : timeDistances) {
            costInTime += timeDistance;
        }
        double previousTimeSum = costInTime;
        
        List<GeoCoordinate> candidates = new ArrayList<GeoCoordinate>();
        double circleSize = initialCircleSize;
        int iteration = 0;
        do{
            candidates = circleCenter.calculateEquiDistantPoints(circleSize, CIRCLE_POINTS_USED);
            double minimumTimeSum = previousTimeSum;
            GeoCoordinate bestCandidate = null;
            for(GeoCoordinate candidate : candidates) {
                List<Long> times = httpService.DistanceInSeconds(candidate, locations);
                int timeSum = 0;
                for(Long time : times) {
                    timeSum += time;
                }
                if(timeSum != 0 && timeSum < minimumTimeSum) {
                    bestCandidate = candidate;
                    minimumTimeSum = timeSum;
                }
            }
            
            int t = 0;
            if(bestCandidate != null) {         //if achieved to reduce time
                circleCenter = bestCandidate;
                previousTimeSum = minimumTimeSum;
                circleSize = circleSize*1.25;
                iteration++;
            }
            else {
                circleSize = circleSize*0.75;
            }
        }while(circleSize > 0.01*initialCircleSize);
        
        return circleCenter;
    }
    
    public GeoCoordinate getMeetingPoint(List<GeoCoordinate> locations) {
        try {
            return getMidPoint(locations);
        } catch (IOException ex) {
            Logger.getLogger(EquiTimeCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
