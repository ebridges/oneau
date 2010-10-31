package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.core.util.Utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableSortedSet;

/**
 * User: EBridges
 * Created: 2010-04-07
 */
public class EphemerisDataFile implements Comparable<EphemerisDataFile> {
    private static final EphemerisDataFile ASCP1600 = new EphemerisDataFile("ascp1600.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1620 = new EphemerisDataFile("ascp1620.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1640 = new EphemerisDataFile("ascp1640.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1660 = new EphemerisDataFile("ascp1660.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1680 = new EphemerisDataFile("ascp1680.405", 1, 1, 1);

    private static final EphemerisDataFile ASCP1700 = new EphemerisDataFile("ascp1700.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1720 = new EphemerisDataFile("ascp1720.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1740 = new EphemerisDataFile("ascp1740.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1760 = new EphemerisDataFile("ascp1760.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1780 = new EphemerisDataFile("ascp1780.405", 1, 1, 1);

    private static final EphemerisDataFile ASCP1800 = new EphemerisDataFile("ascp1600.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1820 = new EphemerisDataFile("ascp1620.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1840 = new EphemerisDataFile("ascp1640.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1860 = new EphemerisDataFile("ascp1660.405", 1, 1, 1);
    private static final EphemerisDataFile ASCP1880 = new EphemerisDataFile("ascp1680.405", 1, 1, 1);

    private static final EphemerisDataFile ASCP1900 = new EphemerisDataFile("ascp1900.405", 230, 2414992.5, 2422320.5);
    private static final EphemerisDataFile ASCP1920 = new EphemerisDataFile("ascp1920.405", 229, 2422320.5, 2429616.5);
    private static final EphemerisDataFile ASCP1940 = new EphemerisDataFile("ascp1940.405", 230, 2429616.5, 2436912.5);
    private static final EphemerisDataFile ASCP1960 = new EphemerisDataFile("ascp1960.405", 229, 2436912.5, 2444208.5);
    private static final EphemerisDataFile ASCP1980 = new EphemerisDataFile("ascp1980.405", 230, 2444208.5, 2451536.5);

    private static final EphemerisDataFile ASCP2000 = new EphemerisDataFile("ascp2000.405", 229, 2451536.5, 2458832.5);
    private static final EphemerisDataFile ASCP2020 = new EphemerisDataFile("ascp2020.405", 229, 2458832.5, 2466128.5);
    private static final EphemerisDataFile ASCP2040 = new EphemerisDataFile("ascp2040.405", 230, 2466128.5, 2473456.5);
    private static final EphemerisDataFile ASCP2060 = new EphemerisDataFile("ascp2060.405", 229, 2473456.5, 2480752.5);
    private static final EphemerisDataFile ASCP2080 = new EphemerisDataFile("ascp2080.405", 229, 2480752.5, 2488048.5);

    private static final EphemerisDataFile ASCP2100 = new EphemerisDataFile("ascp2100.405", 229, 2488048.5, 2495344.5);
    private static final EphemerisDataFile ASCP2120 = new EphemerisDataFile("ascp2120.405", 230, 2495344.5, 2502672.5);
    private static final EphemerisDataFile ASCP2140 = new EphemerisDataFile("ascp2140.405", 229, 2502672.5, 2509968.5);
    private static final EphemerisDataFile ASCP2160 = new EphemerisDataFile("ascp2160.405", 229, 2509968.5, 2517264.5);
    private static final EphemerisDataFile ASCP2180 = new EphemerisDataFile("ascp2180.405", 230, 2517264.5, 2524624.5);
    private static final EphemerisDataFile ASCP2200 = new EphemerisDataFile("ascp2200.405", 1, 1, 1);

    
    private static Map<Range<Double>, EphemerisDataFile> LOOKUP_BY_DATE = new HashMap<Range<Double>, EphemerisDataFile>();
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
        LOOKUP_BY_DATE.put(ASCP1900.getBeginEndDates(), ASCP1900);
        LOOKUP_BY_DATE.put(ASCP1920.getBeginEndDates(), ASCP1920);
        LOOKUP_BY_DATE.put(ASCP1940.getBeginEndDates(), ASCP1940);
        LOOKUP_BY_DATE.put(ASCP1960.getBeginEndDates(), ASCP1960);
        LOOKUP_BY_DATE.put(ASCP1980.getBeginEndDates(), ASCP1980);
        LOOKUP_BY_DATE.put(ASCP2000.getBeginEndDates(), ASCP2000);
        LOOKUP_BY_DATE.put(ASCP2020.getBeginEndDates(), ASCP2020);
        LOOKUP_BY_DATE.put(ASCP2040.getBeginEndDates(), ASCP2040);
        LOOKUP_BY_DATE.put(ASCP2060.getBeginEndDates(), ASCP2060);
        LOOKUP_BY_DATE.put(ASCP2080.getBeginEndDates(), ASCP2080);
        LOOKUP_BY_DATE.put(ASCP2100.getBeginEndDates(), ASCP2100);
        LOOKUP_BY_DATE.put(ASCP2120.getBeginEndDates(), ASCP2120);
        LOOKUP_BY_DATE.put(ASCP2140.getBeginEndDates(), ASCP2140);
        LOOKUP_BY_DATE.put(ASCP2160.getBeginEndDates(), ASCP2160);
        LOOKUP_BY_DATE.put(ASCP2180.getBeginEndDates(), ASCP2180);

        LOOKUP_BY_NAME.put(ASCP1600.getFileName(), ASCP1600);
        LOOKUP_BY_NAME.put(ASCP1620.getFileName(), ASCP1620);
        LOOKUP_BY_NAME.put(ASCP1640.getFileName(), ASCP1640);
        LOOKUP_BY_NAME.put(ASCP1660.getFileName(), ASCP1660);
        LOOKUP_BY_NAME.put(ASCP1680.getFileName(), ASCP1680);

        LOOKUP_BY_NAME.put(ASCP1700.getFileName(), ASCP1700);
        LOOKUP_BY_NAME.put(ASCP1720.getFileName(), ASCP1720);
        LOOKUP_BY_NAME.put(ASCP1740.getFileName(), ASCP1740);
        LOOKUP_BY_NAME.put(ASCP1760.getFileName(), ASCP1760);
        LOOKUP_BY_NAME.put(ASCP1780.getFileName(), ASCP1780);

        LOOKUP_BY_NAME.put(ASCP1800.getFileName(), ASCP1800);
        LOOKUP_BY_NAME.put(ASCP1820.getFileName(), ASCP1820);
        LOOKUP_BY_NAME.put(ASCP1840.getFileName(), ASCP1840);
        LOOKUP_BY_NAME.put(ASCP1860.getFileName(), ASCP1860);
        LOOKUP_BY_NAME.put(ASCP1880.getFileName(), ASCP1880);

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

        LOOKUP_BY_NAME.put(ASCP2200.getFileName(), ASCP2200);
    }

    public static EphemerisDataFile lookupByDate(Double julianTime) {
        if (null != julianTime) {
            for (Map.Entry<Range<Double>, EphemerisDataFile> entry : LOOKUP_BY_DATE.entrySet()) {
                if (entry.getKey().contains(julianTime)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static EphemerisDataFile lookupByName(String filename) {
        if (!Utility.isEmpty(filename) && LOOKUP_BY_NAME.containsKey(filename)) {
            return LOOKUP_BY_NAME.get(filename);
        }
        return null;
    }

    public static Set<EphemerisDataFile> values() {
        return unmodifiableSortedSet(new TreeSet<EphemerisDataFile>(LOOKUP_BY_NAME.values()));
    }

    private Double[] dateRange;
    private String fileName;
    private Integer recordCount;

    private EphemerisDataFile(String fileName, Integer recordCount, double dateRangeBegin, double dateRangeEnd) {
        this.fileName = fileName;
        this.recordCount = recordCount;
        this.dateRange = new Double[]{dateRangeBegin, dateRangeEnd};
    }

    public Double[] getDateRange() {
        return dateRange;
    }

    public Range<Double> getBeginEndDates() {
        return new Range<Double>(dateRange);
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

    /*
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
    */

    int getInterval(Double julianDate) {
        return (int) (Math.floor((julianDate - dateRange[0]) / INTERVAL_DURATION) + 1);
    }

    Double getIntervalStartTime(Double julianDate) {
        return (this.getInterval(julianDate) - 1) * INTERVAL_DURATION + dateRange[0];
    }

    double getSubintervalDuration(HeavenlyBody body) {
        return (INTERVAL_DURATION / body.getNumberOfCoefficientSets());
    }

    int getSubinterval(HeavenlyBody body, double asOf) {
        double subIntervalDuration = this.getSubintervalDuration(body);
        return (int) (Math.floor((asOf - this.getIntervalStartTime(asOf)) / subIntervalDuration) + 1);
    }

    @Override
    public int compareTo(EphemerisDataFile that) {
        return this.fileName.compareTo(that.fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EphemerisDataFile that = (EphemerisDataFile) o;

        return fileName.equals(that.fileName);

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EphemerisDataFile");
        sb.append("{dateRange=").append(dateRange == null ? "null" : Arrays.asList(dateRange).toString());
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", recordCount=").append(recordCount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }
}
