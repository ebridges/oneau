package com.oneau.core.util;

/**
 * Interpolates 
 *
 * User: ebridges
 * Date: Sep 1, 2010
 */
public class ChebyshevTime {
    private Double ephemerisStartTime;
    private Integer ephemerisIntervalSize;

    public ChebyshevTime(Double ephemerisStartTime, Integer ephemerisInterval) {
        this.ephemerisStartTime = ephemerisStartTime;
        this.ephemerisIntervalSize = ephemerisInterval;
    }

    public Double getTimeWithinSubinterval(Integer numCoefficientSets, Double asOf) {
        Integer interval = (int)(Math.floor((asOf - ephemerisStartTime/ephemerisIntervalSize)+1));
        Double intervalStartTime = (interval - 1)*ephemerisIntervalSize + ephemerisStartTime;
        Integer subintervalDuration = ephemerisIntervalSize/numCoefficientSets;
        Integer subinterval = (int)(Math.floor((asOf - intervalStartTime)/subintervalDuration) + 1);
        return 2*(asOf - ((subinterval - 1)*subintervalDuration + intervalStartTime))/subintervalDuration -1;
    }
}
