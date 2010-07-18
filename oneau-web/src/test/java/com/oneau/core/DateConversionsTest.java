package com.oneau.web;

import com.oneau.core.util.Utility;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import static java.lang.String.format;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertEquals;

/**
 * User: ebridges
 * Date: Mar 13, 2010
 */
public class DateConversionsTest {
    private static final Logger logger = Logger.getLogger(DateConversionsTest.class);
    private static final String JULIAN_DAY_DATA = "/julian-days.dat.gz";

    // 1850-01-01T00:00:00.000Z,2396758.50000
    // 1850-01-01T00:02:00.000Z,2396758.50139
    // 1850-01-01T00:03:00.000Z,2396758.50208

    //@Test
    public void testJulianDayConversion() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(getClass().getResourceAsStream(JULIAN_DAY_DATA))));
        String buff;
        while(null !=(buff = reader.readLine())) {
            String[] vals = buff.split(",");
            assertJulianDays(vals[0], Double.valueOf(vals[1]));
        }
    }

    private void assertJulianDays(String expectedDate, Double expectedDays) {
        Double actualDays = Utility.toJulianDay(expectedDate);
        logger.info(format("e [%s] :: a [%s]", expectedDays, actualDays));
        assertEquals(expectedDays, 0.00001, actualDays);
    }

    @Test
    public void testJodaCommutativity() {
        String expected = "1850-01-01T00:11:11.000Z";  // JD 2396758.50777

        DateTime expectedDate = new DateTime(expected).withZone(UTC);
        Long expectedMillis = expectedDate.getMillis();

        DateTime actualDate = new DateTime(expectedMillis).withZone(UTC);
        Long actualMillis = actualDate.getMillis();
        String actual = actualDate.toString();

        logger.info(format("expected [%s] :: actual [%s]", expected, actual));
        assertEquals(expectedDate, actualDate);
        assertEquals(expectedMillis, actualMillis);
        assertEquals(expected, actual);
    }
}
