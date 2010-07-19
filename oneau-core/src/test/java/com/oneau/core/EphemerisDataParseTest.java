package com.oneau.core;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.oneau.core.util.AssertionUtil.assertArraysEqual;
import static org.junit.Assert.assertNotNull;

/**
 * User: EBridges
 * Created: 2010-04-08
 */
public class EphemerisDataParseTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataParseTest.class);
    private static final EphemerisDataFile DATA_FILE= EphemerisDataFile.lookupByName("ascp2000.405");

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
