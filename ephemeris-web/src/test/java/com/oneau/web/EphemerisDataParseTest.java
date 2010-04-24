package com.oneau.web;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.String.format;

import static com.oneau.web.util.AssertionUtil.assertArraysEqual;
import com.oneau.web.util.AssertionUtil;
import static org.junit.Assert.assertNotNull;

/**
 * User: EBridges
 * Created: 2010-04-08
 */
public class EphemerisDataParseTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataParseTest.class);
    private static final EphemerisDataFile DATA_FILE= EphemerisDataFile.lookupByName("ASCP2000.txt");

    @Before
    public void setUp() {
    }

    @Test
    public void testCompareParsing() throws IOException {
        EphemerisData data = new EphemerisData(DATA_FILE);
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation();
        Double[] coefficients = expected.parseEphemerisCoefficients(DATA_FILE);
        assertNotNull(coefficients);
        assertNotNull(data.getEphemerisCoefficients());
        assertArraysEqual(coefficients, data.getEphemerisCoefficients());
    }

}
