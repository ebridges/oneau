package com.oneau.web.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public final class Utility {

    public static boolean isEmpty(String s) {
        return null == s || s.length() < 1;
    }

    public static String toCsv(double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for(int i=0; i<o.length; i++) {
            if(i>0) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }


    public static String toCsv(Double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for(int i=0; i<o.length; i++) {
            if(i>0) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }


    public static double toJulianDay(String isoDate) {
        DateTime d = new DateTime(isoDate).withZone(DateTimeZone.UTC);
        return toJulianDay(
                d.getYear(),
                d.getMonthOfYear(),
                d.getDayOfMonth(),
                d.getHourOfDay(),
                d.getMinuteOfHour(),
                d.getSecondOfMinute(),
                0.0
        );
    }

    public static double toJulianDay (double year, double month, double day, double hour, double minute, double second, double tz) {
        double dayDecimal, julianDay, a;

        dayDecimal = day + (hour - tz + (minute + second/60.0)/60.0)/24.0;

        if (month < 3) {
            month += 12;
            year--;
        }

        julianDay = Math.floor(365.25*(year+4716.0)) + Math.floor(30.6001*(month+1)) + dayDecimal - 1524.5;

        if (julianDay > 2299160.0) {
            a = Math.floor(year/100);
            julianDay += (2 - a + Math.floor(a/4));
        }

        return julianDay;
    }

    private Utility() {
    }

    public static String toString(Object[] objects) {
        StringBuilder sb = new StringBuilder(objects.length*32);
        boolean isFirst = true;
        for(Object o : objects) {
            if(isFirst) {
                isFirst = false;
            } else {
                sb.append(',');
            }
            sb.append(String.valueOf(o));
        }
        return sb.toString();
    }

    public static boolean isEmpty(String[] strings) {
        return null == strings || strings.length < 1;
    }
}
