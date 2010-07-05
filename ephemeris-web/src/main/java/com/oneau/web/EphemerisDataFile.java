package com.oneau.web;

import com.oneau.web.util.HeavenlyBody;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.oneau.web.util.Utility.isBetween;
import static com.oneau.web.util.Utility.isEmpty;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableSortedSet;

/**
 * User: EBridges
 * Created: 2010-04-07
 */
public class EphemerisDataFile implements Comparable<EphemerisDataFile> {
    private static final Logger logger = Logger.getLogger(EphemerisDataFile.class);

    private static final EphemerisDataFile ASCP1900 = new EphemerisDataFile("ASCP1900.txt", 230, 2414992.5, 2422320.5);
    private static final EphemerisDataFile ASCP1920 = new EphemerisDataFile("ASCP1920.txt", 229, 2422320.5, 2429616.5);
    private static final EphemerisDataFile ASCP1940 = new EphemerisDataFile("ASCP1940.txt", 230, 2429616.5, 2436912.5);
    private static final EphemerisDataFile ASCP1960 = new EphemerisDataFile("ASCP1960.txt", 229, 2436912.5, 2444208.5);
    private static final EphemerisDataFile ASCP1980 = new EphemerisDataFile("ASCP1980.txt", 230, 2444208.5, 2451536.5);
    private static final EphemerisDataFile ASCP2000 = new EphemerisDataFile("ASCP2000.txt", 229, 2451536.5, 2458832.5);
    private static final EphemerisDataFile ASCP2020 = new EphemerisDataFile("ASCP2020.txt", 229, 2458832.5, 2466128.5);
    private static final EphemerisDataFile ASCP2040 = new EphemerisDataFile("ASCP2040.txt", 230, 2466128.5, 2473456.5);
    private static final EphemerisDataFile ASCP2060 = new EphemerisDataFile("ASCP2060.txt", 229, 2473456.5, 2480752.5);
    private static final EphemerisDataFile ASCP2080 = new EphemerisDataFile("ASCP2080.txt", 229, 2480752.5, 2488048.5);
    private static final EphemerisDataFile ASCP2100 = new EphemerisDataFile("ASCP2100.txt", 229, 2488048.5, 2495344.5);
    private static final EphemerisDataFile ASCP2120 = new EphemerisDataFile("ASCP2120.txt", 230, 2495344.5, 2502672.5);
    private static final EphemerisDataFile ASCP2140 = new EphemerisDataFile("ASCP2140.txt", 229, 2502672.5, 2509968.5);
    private static final EphemerisDataFile ASCP2160 = new EphemerisDataFile("ASCP2160.txt", 229, 2509968.5, 2517264.5);
    private static final EphemerisDataFile ASCP2180 = new EphemerisDataFile("ASCP2180.txt", 230, 2517264.5, 2524624.5);

    private static Map<double[], EphemerisDataFile> LOOKUP_BY_DATE = new HashMap<double[], EphemerisDataFile>();
    private static Map<String, EphemerisDataFile> LOOKUP_BY_NAME = new HashMap<String, EphemerisDataFile>();

    /**
     * Chebyshev coefficients for the DE405 ephemeris are contained in the files "ASCPxxxx.txt".
     * These files are broken into intervals of length "interval_duration", in days.
     */
    public final static int INTERVAL_DURATION = 32;

    /**
     * Each interval contains an interval number, length, start and end jultimes, and Chebyshev coefficients.
     * We keep only the coefficients.
     */
    public final static int NUMBERS_PER_INTERVAL = 816;

    static {
        LOOKUP_BY_DATE.put(ASCP1900.getDateRange(), ASCP1900);
        LOOKUP_BY_DATE.put(ASCP1920.getDateRange(), ASCP1920);
        LOOKUP_BY_DATE.put(ASCP1940.getDateRange(), ASCP1940);
        LOOKUP_BY_DATE.put(ASCP1960.getDateRange(), ASCP1960);
        LOOKUP_BY_DATE.put(ASCP1980.getDateRange(), ASCP1980);
        LOOKUP_BY_DATE.put(ASCP2000.getDateRange(), ASCP2000);
        LOOKUP_BY_DATE.put(ASCP2020.getDateRange(), ASCP2020);
        LOOKUP_BY_DATE.put(ASCP2040.getDateRange(), ASCP2040);
        LOOKUP_BY_DATE.put(ASCP2060.getDateRange(), ASCP2060);
        LOOKUP_BY_DATE.put(ASCP2080.getDateRange(), ASCP2080);
        LOOKUP_BY_DATE.put(ASCP2100.getDateRange(), ASCP2100);
        LOOKUP_BY_DATE.put(ASCP2120.getDateRange(), ASCP2120);
        LOOKUP_BY_DATE.put(ASCP2140.getDateRange(), ASCP2140);
        LOOKUP_BY_DATE.put(ASCP2160.getDateRange(), ASCP2160);
        LOOKUP_BY_DATE.put(ASCP2180.getDateRange(), ASCP2180);

        LOOKUP_BY_NAME.put(ASCP1900.getFileName(), ASCP1900);
        LOOKUP_BY_NAME.put(ASCP1920.getFileName(), ASCP1920);
        LOOKUP_BY_NAME.put(ASCP1940.getFileName(), ASCP1940);
        LOOKUP_BY_NAME.put(ASCP1960.getFileName(), ASCP1960);
        LOOKUP_BY_NAME.put(ASCP1980.getFileName(), ASCP1980);
        LOOKUP_BY_NAME.put(ASCP2000.getFileName(), ASCP2000);
        LOOKUP_BY_NAME.put(ASCP2020.getFileName(), ASCP2020);
        LOOKUP_BY_NAME.put(ASCP2040.getFileName(), ASCP2040);
        LOOKUP_BY_NAME.put(ASCP2060.getFileName(), ASCP2060);
        LOOKUP_BY_NAME.put(ASCP2080.getFileName(), ASCP2080);
        LOOKUP_BY_NAME.put(ASCP2100.getFileName(), ASCP2100);
        LOOKUP_BY_NAME.put(ASCP2120.getFileName(), ASCP2120);
        LOOKUP_BY_NAME.put(ASCP2140.getFileName(), ASCP2140);
        LOOKUP_BY_NAME.put(ASCP2160.getFileName(), ASCP2160);
        LOOKUP_BY_NAME.put(ASCP2180.getFileName(), ASCP2180);
    }

    public static EphemerisDataFile lookupByDate(Double julianTime) {
        if (null != julianTime) {
            for (Map.Entry<double[], EphemerisDataFile> entry : LOOKUP_BY_DATE.entrySet()) {
                if (isBetween(julianTime, entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static EphemerisDataFile lookupByName(String filename) {
        if (!isEmpty(filename) && LOOKUP_BY_NAME.containsKey(filename)) {
            return LOOKUP_BY_NAME.get(filename);
        }
        return null;
    }

    public static Set<EphemerisDataFile> values() {
        return unmodifiableSortedSet(new TreeSet<EphemerisDataFile>(LOOKUP_BY_NAME.values()));
    }

    private double[] dateRange;
    private String fileName;
    private Integer recordCount;

    private EphemerisDataFile(String fileName, Integer recordCount, double dateRangeBegin, double dateRangeEnd) {
        this.fileName = fileName;
        this.recordCount = recordCount;
        this.dateRange = new double[]{dateRangeBegin, dateRangeEnd};
    }

    public double[] getDateRange() {
        return dateRange;
    }

    public double getBeginDate() {
        return dateRange[0];
    }

    public double getEndDate() {
        return dateRange[1];
    }

    public String getFileName() {
        return fileName;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public boolean existsOnClasspath() {
        return getClass().getResourceAsStream(this.fileName) != null;
    }

    public boolean existsAtLocation(String location) {
        File f = new File(location, this.fileName);
        return f.exists();
    }

    public boolean exists() {
        File f = new File(this.fileName);
        return f.exists();
    }

    public int getInterval(Double julianDate) {
        return (int) (Math.floor((julianDate - dateRange[0]) / INTERVAL_DURATION) + 1);
    }

    public Double getIntervalStartTime(Double julianDate) {
        return (this.getInterval(julianDate) - 1) * INTERVAL_DURATION + dateRange[0];
    }

    public double getSubintervalDuration(HeavenlyBody body) {
        return (INTERVAL_DURATION / body.getNumberOfCoefficientSets());
    }

    public int getSubinterval(HeavenlyBody body, double asOf) {
        double subIntervalDuration = this.getSubintervalDuration(body);
        int subinterval = (int) (Math.floor((asOf - this.getIntervalStartTime(asOf)) / subIntervalDuration) + 1);
        if (logger.isDebugEnabled()) {
            logger.debug(format("asOf(%f) beginDate(%f) subIntervalDuration(%f) subinterval(%d)", asOf, this.getBeginDate(), subIntervalDuration, subinterval));
        }
        return subinterval;
    }

    @Override
    public int compareTo(EphemerisDataFile that) {
        return this.fileName.compareTo(that.fileName);
    }
}
