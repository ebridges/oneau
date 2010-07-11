package com.oneau.web.util;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static com.oneau.web.util.Constants.ISO_DATE_PATTERN;
import static java.lang.String.format;
import static java.lang.System.arraycopy;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public final class Utility {
    private static final Logger logger = Logger.getLogger(Utility.class);

    private Utility() {
    }

    public static boolean isEmpty(String s) {
        return null == s || s.length() < 1;
    }

    public static Double[] newDouble(int size) {
        Double[] d = new Double[size];
        for (int i = 0; i < size; i++) {
            d[i] = 0.0;
        }
        return d;
    }

    public static String toCsv(double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for (int i = 0; i < o.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }


    public static String toCsv(Double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for (int i = 0; i < o.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }

    public static Double toJulianDay() {
        DateTime d = new DateTime().withZone(DateTimeZone.UTC);
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

    public static double toJulianDay(double year, double month, double day, double hour, double minute, double second, double tz) {
        double dayDecimal, julianDay, a;

        dayDecimal = day + (hour - tz + (minute + second / 60.0) / 60.0) / 24.0;

        if (month < 3) {
            month += 12;
            year--;
        }

        julianDay = Math.floor(365.25 * (year + 4716.0)) + Math.floor(30.6001 * (month + 1)) + dayDecimal - 1524.5;

        if (julianDay > 2299160.0) {
            a = Math.floor(year / 100);
            julianDay += (2 - a + Math.floor(a / 4));
        }

        return julianDay;
    }

    public static String toString(Object[] objects) {
        StringBuilder sb = new StringBuilder(objects.length * 32);
        boolean isFirst = true;
        for (Object o : objects) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(',');
            }
            sb.append(String.valueOf(o));
        }
        return sb.toString();
    }

    public static boolean isEmpty(Object[] v) {
        return (null == v || v.length < 1);
    }

    public static boolean contains(Object[] es, Object ee) {
        for (Object e : es) {
            if (e == ee) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAny(Object[] es, Object... ee) {
        if (isEmpty(es) || isEmpty(ee)) {
            return false;
        }
        for (Object e : es) {
            if (e == ee[0]) {
                return true;
            }
        }
        if (ee.length > 1) {
            return containsAny(es, rest(ee));
        }
        return false;
    }

    public static boolean containsAll(Object[] es, Object... ee) {
        //  System.out.printf("es: %s, ee: %s\n", toString(es), toString(ee));
        if (isEmpty(es) || isEmpty(ee)) {
            return false;
        }
        if (ee.length > 1) {
            return containsAll(es, first(ee)) && containsAll(es, rest(ee));
        } else {
            for (Object e : es) {
                if (e == ee[0]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Object first(Object[] ee) {
        if (isEmpty(ee)) {
            return null;
        } else {
            return ee[0];
        }
    }

    public static Object[] rest(Object[] e) {
        if (isEmpty(e)) {
            return e;
        }
        Object[] ee = new Object[e.length - 1];
        arraycopy(e, 1, ee, 0, e.length - 1);
        return ee;
    }

    public static Double[] parseDate(String isoDate) {
        Matcher m = ISO_DATE_PATTERN.matcher(isoDate);
        if (m.matches()) {
            int cnt = m.groupCount();
            Double[] dateFields = new Double[cnt];
            for (int i = 1; i <= cnt; i++) {
                Double v = new Double(m.group(i));
                dateFields[i - 1] = v;
            }
            return dateFields;
        } else {
            throw new IllegalArgumentException(format("Invalid date format [%s]", isoDate));
        }
    }

    public static HeavenlyBody[] toHeavenlyBody(String... bodyNames) {

        HeavenlyBody[] h = null;
        boolean bodyFound = false;
        if (null != bodyNames) {
            List<HeavenlyBody> bodies = new ArrayList<HeavenlyBody>(bodyNames.length);
            for (String name : bodyNames) {
                HeavenlyBody body = HeavenlyBody.lookup(name);
                if (null != body) {
                    bodies.add(body);
                    bodyFound = true;
                } else {
                    logger.warn(format("no body found with name [%s]", name));
                }
            }
            if (bodyFound)
                h = bodies.toArray(new HeavenlyBody[bodyNames.length]);
        }

        if (!bodyFound)
            h = HeavenlyBody.values();

        assert null != h;
        logger.info(Arrays.toString(h));

        return h;
    }

    public static boolean isBetween(double asOf, double[] range) {
        return (range[0] < asOf) && (asOf < range[range.length - 1]);
    }

    public static <T> T throwIfNull(String field, T t) {
        if (null == t) {
            throw new IllegalArgumentException(format("%s cannot be null", field));
        }
        return t;
    }

    public static Double convertDouble(String field) {
        if(isEmpty(field)) {
            field = "0.0";
        }
        return Double.valueOf(field);
    }

    public static Integer convertInteger(String field) {
        if(isEmpty(field)) {
            field = "0";
        }
        return Integer.valueOf(field);
    }
}
