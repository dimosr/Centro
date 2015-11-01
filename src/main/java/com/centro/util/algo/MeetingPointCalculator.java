package com.centro.util.algo;

import com.centro.util.GeoCoordinate;
import java.util.List;

public interface MeetingPointCalculator {
    public GeoCoordinate getMeetingPoint(List<GeoCoordinate> locations);
}
