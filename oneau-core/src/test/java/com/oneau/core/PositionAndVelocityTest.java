package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.toCsv;
import com.oneau.core.util.AssertionUtil;
import com.oneau.data.DAOFactory;
import com.oneau.data.EphemerisDAO;

import static java.lang.String.format;

/**
 * User: EBridges
 * Created: 2010-04-08
 */
public class PositionAndVelocityTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataParseTest.class.getName());
    private static final EphemerisDataFile DATA_FILE= EphemerisDataFile.lookupByName(format(EphemerisDataFile.EPHEMERIS_FILE_ROOT, "ascp2000.405"));
    private static final Double JAN_01_2000 = 2451544.5;
    private static final Double JAN_01_2010 = 2455197.5;

    //private static final String TEST_DATE_FILE = "/julian-days.dat.gz";
    private static final String TEST_DATE_FILE = "/dates.txt";

    private Ephemeris underTest;

    @Before
    public void setUp() {
    	EphemerisDAO dao = DAOFactory.instance().getEphemerisDAO();
        underTest = new Ephemeris(dao);
    }

    @Test
    public void testCalculatePositionAndVelocity_SingleDateMultiplePlanets() throws IOException {
        EphemerisData data = new EphemerisData(DATA_FILE);
        Double[] ephemeris_coefficients = data.getEphemerisCoefficients();
        Double[] ephemeris_dates = DATA_FILE.getDateRange();

        for(HeavenlyBody body : HeavenlyBody.orderedByIndex()) {
            if(body.isBody()) {
                EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation(ephemeris_coefficients, ephemeris_dates);
                double[] expectedEphemerisR = new double[4];
                double[] expectedEphemerisRPrime = new double[4];
                logger.info(format("Calculating expected position & velocity for %s from reference implementation.", body.getName()));
                expected.getPlanetPositionAndVelocity(JAN_01_2010, body.getId(), expectedEphemerisR, expectedEphemerisRPrime);
                logger.info(format("Calculating actual position & velocity for %s from developed code.", body.getName()));
                PositionAndVelocity actual = underTest.getPlanetPositionAndVelocity(JAN_01_2010, body);

                // note: legacy code uses fortran-conventions for array indices -- therefore the index to be used for
                //       the heavenlyBody needs to be +1 from the 0-based "getIndex()" method -- hence the special 'copy' method
                Double[] expectedPosition = copy(expectedEphemerisR);
                Double[] expectedVelocity = copy(expectedEphemerisRPrime);

                // de#  -- date -- -- jed -- t# c# x# -- coordinate ---
                // 405  2000.01.01 2451544.5 10 13  2      -0.0015614617894

                logger.info(format("expected value for position: [%s]", toCsv(expectedPosition)));
                logger.info(format("actual   value for position: [%s]", toCsv(actual.getPosition())));
                logger.info(format("expected value for velocity: [%s]", toCsv(expectedVelocity)));
                logger.info(format("actual   value for velocity: [%s]", toCsv(actual.getVelocity())));

                AssertionUtil.assertArraysEqual(expectedPosition, actual.getPosition());
                AssertionUtil.assertArraysEqual(expectedVelocity, actual.getVelocity());
            }
        }
    }


    @Test
    public void testCalculatePositionAndVelocity_OneDate() throws IOException {
        EphemerisData data = new EphemerisData(DATA_FILE);
        Double[] ephemeris_coefficients = data.getEphemerisCoefficients();
        Double[] ephemeris_dates = DATA_FILE.getDateRange();
        
        logger.info("dates: " + toCsv(ephemeris_dates));
        for(int i = 0 ; i<5; i++) {
        	logger.info("    coeff["+i+"]: "+ephemeris_coefficients[i]);
        }
        
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation(ephemeris_coefficients, ephemeris_dates);
        double[] expectedEphemerisR = new double[4];
        double[] expectedEphemerisRPrime = new double[4];
        logger.info("Calculating expected position & velocity from reference implementation.");
        expected.getPlanetPositionAndVelocity(JAN_01_2000, HeavenlyBody.MERCURY.getId(), expectedEphemerisR, expectedEphemerisRPrime);
        logger.info("Calculating actual position & velocity from developed code.");
        PositionAndVelocity actual = underTest.getPlanetPositionAndVelocity(JAN_01_2000, HeavenlyBody.MERCURY);

        // note: legacy code uses fortran-conventions for array indices -- therefore the index to be used for
        //       the heavenlyBody needs to be +1 from the 0-based "getIndex()" method -- hence the special 'copy' method
        Double[] expectedPosition = copy(expectedEphemerisR);
        Double[] expectedVelocity = copy(expectedEphemerisRPrime);

        // de#  -- date -- -- jed -- t# c# x# -- coordinate ---
        // 405  2000.01.01 2451544.5 10 13  2      -0.0015614617894
        
        logger.info(format("expected value for position: [%s]", toCsv(expectedPosition)));
        logger.info(format("actual   value for position: [%s]", toCsv(actual.getPosition())));
        logger.info(format("expected value for velocity: [%s]", toCsv(expectedVelocity)));
        logger.info(format("actual   value for velocity: [%s]", toCsv(actual.getVelocity())));

        AssertionUtil.assertArraysEqual(expectedPosition, actual.getPosition());
        AssertionUtil.assertArraysEqual(expectedVelocity, actual.getVelocity());
    }

    @Test
    public void testCalculatePositionAndVelocity_MultipleDates() throws IOException {
        BufferedReader r = loadData(TEST_DATE_FILE);
        String line;
        EphemerisData data = new EphemerisData(DATA_FILE);
        Double[] ephemeris_coefficients = data.getEphemerisCoefficients();
        Double[] ephemeris_dates = DATA_FILE.getDateRange();
        EphemerisReferenceImplementation expected = new EphemerisReferenceImplementation(ephemeris_coefficients, ephemeris_dates);

        try {
            while((line = r.readLine()) != null) {
                String[] vals = line.split(",");
                if(vals.length == 2) {
                    Double asOf = Double.valueOf(vals[1]);
                    if(DATA_FILE.getBeginEndDates().contains(asOf)) {
                        double[] expectedEphemerisR = new double[4];
                        double[] expectedEphemerisRPrime = new double[4];
                        logger.info("Calculating expected position & velocity from reference implementation.");
                        expected.getPlanetPositionAndVelocity(asOf, HeavenlyBody.SUN.getId(), expectedEphemerisR, expectedEphemerisRPrime);

                        logger.info("Calculating actual position & velocity from develped code.");
                        PositionAndVelocity actual = underTest.getPlanetPositionAndVelocity(asOf, HeavenlyBody.SUN);

                        // note: legacy code uses fortran-conventions for array indices -- therefore the index to be used for
                        //       the heavenlyBody needs to be +1 from the 0-based "getIndex()" method -- hence the special 'copy' method
                        AssertionUtil.assertArraysEqual(copy(expectedEphemerisR), actual.getPosition());
                        AssertionUtil.assertArraysEqual(copy(expectedEphemerisRPrime), actual.getVelocity());
                    } else {
                        logger.fine(format("skipping date [%f] since it's outside date range of data [%f:%f].", asOf, DATA_FILE.getBeginDate(), DATA_FILE.getEndDate()));
                    }
                } else {
                    logger.warning(format("skipping malformed line [%s]", line));
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
