package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import com.oneau.core.util.Utility;
import com.oneau.web.util.AssertionUtil;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import static com.oneau.core.util.Utility.toCsv;
import static com.oneau.web.util.AssertionUtil.assertArraysEqual;
import static java.lang.String.format;

/**
 * User: EBridges
 * Created: 2010-04-08
 */
public class PositionAndVelocityTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataParseTest.class);
    private static final EphemerisDataFile DATA_FILE= EphemerisDataFile.lookupByName("ASCP2000.txt");
    //private static final Double TEST_DATE = 2451545.0; // 2000-01-01T12:00:00Z
    //private static final Double TEST_DATE = 2455302.500000000;
    private static final Double TEST_DATE = 2452210.33056;

    //private static final String TEST_DATE_FILE = "/julian-days.dat.gz";
    private static final String TEST_DATE_FILE = "/dates.txt";

    private Ephemeris underTest;

    @Before
    public void setUp() {
        underTest = new Ephemeris();
    }

    @Test
    public void testCalculatePositionAndVelocity_OneDate() throws IOException {
        EphemerisData data = new EphemerisData(DATA_FILE);
        Double[] ephemeris_coefficients = data.getEphemerisCoefficients();
        double[] ephemeris_dates = DATA_FILE.getDateRange();
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation(AssertionUtil.copy(ephemeris_coefficients), ephemeris_dates);
        double[] expectedEphemerisR = new double[4];
        double[] expectedEphemerisRPrime = new double[4];
        logger.info("Calculating expected position & velocity from reference implementation.");
        expected.getPlanetPositionAndVelocity(TEST_DATE, HeavenlyBody.SUN.getId(), expectedEphemerisR, expectedEphemerisRPrime);
        logger.info("Calculating actual position & velocity from develped code.");
        PositionAndVelocity actual = underTest.getPlanetPositionAndVelocity(TEST_DATE, HeavenlyBody.SUN);

        // note: legacy code uses fortran-conventions for array indices -- therefore the index to be used for
        //       the heavenlyBody needs to be +1 from the 0-based "getIndex()" method -- hence the special 'copy' method
        Double[] expectedPosition = copy(expectedEphemerisR);
        Double[] expectedVelocity = copy(expectedEphemerisRPrime);

        logger.info(format("expected value for position: [%s]", toCsv(expectedPosition)));
        logger.info(format("actual   value for position: [%s]", toCsv(actual.getPosition())));
        logger.info(format("expected value for velocity: [%s]", toCsv(expectedVelocity)));
        logger.info(format("actual   value for velocity: [%s]", toCsv(actual.getVelocity())));

        assertArraysEqual(expectedPosition, actual.getPosition());
        assertArraysEqual(expectedVelocity, actual.getVelocity());
    }

    @Test
    public void testCalculatePositionAndVelocity_MultipleDates() throws IOException {
        BufferedReader r = loadData(TEST_DATE_FILE);
        String line;
        EphemerisData data = new EphemerisData(DATA_FILE);
        Double[] ephemeris_coefficients = data.getEphemerisCoefficients();
        double[] ephemeris_dates = DATA_FILE.getDateRange();
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation(AssertionUtil.copy(ephemeris_coefficients), ephemeris_dates);

        try {
            while((line = r.readLine()) != null) {
                String[] vals = line.split(",");
                if(vals.length == 2) {
                    Double asOf = Double.valueOf(vals[1]);
                    if(Utility.isBetween(asOf, DATA_FILE.getDateRange())) {
                        double[] expectedEphemerisR = new double[4];
                        double[] expectedEphemerisRPrime = new double[4];
                        logger.info("Calculating expected position & velocity from reference implementation.");
                        expected.getPlanetPositionAndVelocity(asOf, HeavenlyBody.SUN.getId(), expectedEphemerisR, expectedEphemerisRPrime);

                        logger.info("Calculating actual position & velocity from develped code.");
                        PositionAndVelocity actual = underTest.getPlanetPositionAndVelocity(asOf, HeavenlyBody.SUN);

                        // note: legacy code uses fortran-conventions for array indices -- therefore the index to be used for
                        //       the heavenlyBody needs to be +1 from the 0-based "getIndex()" method -- hence the special 'copy' method
                        assertArraysEqual(copy(expectedEphemerisR), actual.getPosition());
                        assertArraysEqual(copy(expectedEphemerisRPrime), actual.getVelocity());
                    } else {
                        logger.debug(format("skipping date [%f] since it's outside date range of data [%f:%f].", asOf, DATA_FILE.getBeginDate(), DATA_FILE.getEndDate()));
                    }
                } else {
                    logger.warn(format("skipping malformed line [%s]", line));
                }
            }
        } finally {
            if(null != r)
                r.close();
        }
    }

    private BufferedReader loadData(String file) throws IOException {
        InputStream is = getClass().getResourceAsStream(file);
        if(file.endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(is)));
        } else {
            return new BufferedReader(new InputStreamReader(is));
        }
    }

    private Double[] copy(double[] doubles) {
        // in legacy code, all arrays are initialized to +1 their needed size.
        // this method accommodates that so that they can be equally compared.
        Double[] copy = new Double[doubles.length-1];
        for(int i=0; i<doubles.length-1; i++) {
            copy[i] = doubles[i+1];
        }
        return copy;
    }
}
