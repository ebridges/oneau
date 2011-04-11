package com.oneau.core.util;

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
    public static final Pattern ISO_DATE_RE_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})");
    
    /**
     * Date format for ISO8601 date for parsing by SimpleDateFormat
     */
    public static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSz";
    
    /**
     * Date format used in the TESTPO.405 test data file
     */
    public static final String TESTPO_DATE_PATTERN = "yyyy.MM.dd";

    /**
     * Servlet init-param for identifying data files intended to be pre-loaded.
     */
    public static final String EPHEMERIS_DATA = "ephemeris-data";

    /**
     * Servlet context attribute where an instance of the calculator is stored.
     */
    public static final String DECLINATION_CALCULATOR = "declination-calculator";


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
    public static final String ELEVATION_PARAM = "ele";

    public static final String JDBC_URL_KEY = "jdbc_url";
    public static final String JDBC_USERNAME_KEY = "jdbc_username";
    public static final String JDBC_PASSWORD_KEY = "jdbc_password";
    public static final String JDBC_DRIVER_KEY = "jdbc_driver";

    private Constants() {
    }
}
