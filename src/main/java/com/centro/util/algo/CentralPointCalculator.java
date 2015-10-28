package com.centro.util.algo;

import com.centro.util.GeoCoordinate;
import java.util.List;

public interface CentralPointCalculator {
    public GeoCoordinate getCentralPoint(List<GeoCoordinate> locations);
}
