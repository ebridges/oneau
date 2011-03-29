package com.oneau.data;

/**
 * User: ebridges
 * Date: Nov 21, 2010
 */
public class EphemerisIntervalNotFound extends EphemerisLookupError {
    public Double getJulianDate() {
        return julianDate;
    }

    private final Double julianDate;

    public EphemerisIntervalNotFound(Double julianDate) {
        this.julianDate = julianDate;
    }

    @Override
    public String toString() {
        return (new StringBuilder())
                .append("Ephemeris Interval Not Found for Julian Date ")
                .append(julianDate)
                .toString();
    }
}
