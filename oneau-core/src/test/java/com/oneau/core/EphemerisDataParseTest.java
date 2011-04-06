package com.oneau.core;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.oneau.core.util.AssertionUtil;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

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
    	logger.debug("testCompareParsing() called.");
        EphemerisData data = new EphemerisData(DATA_FILE);
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation();
        Double[] coefficients = expected.parseEphemerisCoefficients(DATA_FILE);
        assertNotNull(coefficients);
        assertNotNull(data.getEphemerisCoefficients());
        AssertionUtil.assertArraysEqual(coefficients, data.getEphemerisCoefficients());
    }

}
