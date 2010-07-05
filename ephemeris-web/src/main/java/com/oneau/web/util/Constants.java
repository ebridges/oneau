package com.oneau.web.util;

import java.util.regex.Pattern;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public final class Constants {
    /**
     * One astronomic unit in km.
     */
    public static final double AU = 149597870.691;

    /**
     * Ratio of the mass of the Earth to the mass of the Moon
     */
    public final static double EMrat = 81.30056;

    /**
     * Regular expression that can match and parse an ISO8601 formatted date (yyyy-mm-ddThh:mm:ss).
     */
    public static final Pattern ISO_DATE_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})");

    /**
     * Servlet init-param for identifying data files intended to be pre-loaded.
     */
    public static final String EPHEMERIS_DATA = "ephemeris-data";

    /**
     * Servlet context attribute where an instance of the calculator is stored.
     */
    public static final String DECLENSION_CALCULATOR = "declension-calculator";


    public static final String DATE_PARAM = "isoDate";
    public static final String JULIAN_DATE_PARAM = "julianDate";
    public static final String BODY_NAME_PARAM = "body";
    public static final String MONTH_PARAM = "month";
    public static final String DAY_PARAM = "day";
    public static final String YEAR_PARAM = "year";
    public static final String HOUR_PARAM = "hour";
    public static final String MIN_PARAM = "minute";
    public static final String SEC_PARAM = "second";
    public static final String RESPONSE_CONTENT_TYPE_PARAM = "responseType";
    public static final String RESPONSE_UNITS_PARAM = "responseUnits";
    public static final String LONGITUDE_PARAM = "lon";
    public static final String LATITUDE_PARAM = "lat";
    public static final String ELEVATION_PARAM = "el";

    private Constants() {
    }
}
