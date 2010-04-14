package com.oneau.web;

import static com.oneau.web.util.AssertionUtil.assertArraysEqual;
import com.oneau.web.util.HeavenlyBody;
import org.junit.Before;
import org.junit.Test;
import org.apache.log4j.Logger;

import java.io.IOException;
import static java.lang.String.format;

/**
 * User: EBridges
 * Created: 2010-04-08
 */
public class PositionAndVelocityTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataParseTest.class);
    private static final EphemerisDataFile DATA_FILE= EphemerisDataFile.lookupByName("ASCP2000.txt");
    private static final Double TEST_DATE = 2451545.0; // 2000-01-01T12:00:00Z
    private Ephemeris ephemeris;

    @Before
    public void setUp() {
        ephemeris = new Ephemeris();
    }

    @Test
    public void testCalculatePosition() throws IOException {
        EphemerisData data = new EphemerisData(DATA_FILE);
        ephemeris_coefficients = data.getEphemerisCoefficients();
        ephemeris_dates = DATA_FILE.getDateRange();
        double[] expectedEphemerisR = new double[4];
        double[] expectedEphemerisRPrime = new double[4];
        logger.info("Calculating expected position & velocity from reference implementation.");
        this.getPlanetPositionAndVelocity(TEST_DATE, HeavenlyBody.SUN.getIndex(), expectedEphemerisR, expectedEphemerisRPrime);
        logger.info("Calculating actual position & velocity from develped code.");
        PositionAndVelocity actual = ephemeris.getPlanetPositionAndVelocity(TEST_DATE, HeavenlyBody.SUN);
        assertArraysEqual(copy(expectedEphemerisR), actual.getPosition());
        assertArraysEqual(copy(expectedEphemerisRPrime), actual.getVelocity());
    }

    private Double[] copy(double[] doubles) {
        // in legacy code, all arrays are initialized to +1 their needed size.
        // this method accommodates that so that they can be equally compared.
        Double[] copy = new Double[doubles.length-1];
        for(int i=0; i<doubles.length-1; i++) {
            copy[i] = doubles[i];
        }
        return copy;
    }

    void getPlanetPositionAndVelocity(double jultime, int heavenlyBody, double ephemeris_r[], double ephemeris_rprime[]) throws IOException {
        int interval;
        int numbers_to_skip;
        int pointer;
        int j;
        int k;
        int subinterval;

        double interval_start_time;
        double subinterval_duration;
        double chebyshev_time;

        double[] position_poly = new double[20];
        double[][] coef = new double[4][20];
        double[] velocity_poly = new double[20];

        int[] number_of_coef_sets = new int[12];
        int[] number_of_coefs = new int[12];

        /*
            Initialize arrays
          */
        number_of_coefs[1] = number_of_coefs_1;
        number_of_coefs[2] = number_of_coefs_2;
        number_of_coefs[3] = number_of_coefs_3;
        number_of_coefs[4] = number_of_coefs_4;
        number_of_coefs[5] = number_of_coefs_5;
        number_of_coefs[6] = number_of_coefs_6;
        number_of_coefs[7] = number_of_coefs_7;
        number_of_coefs[8] = number_of_coefs_8;
        number_of_coefs[9] = number_of_coefs_9;
        number_of_coefs[10] = number_of_coefs_10;
        number_of_coefs[11] = number_of_coefs_11;
        number_of_coef_sets[1] = number_of_coef_sets_1;
        number_of_coef_sets[2] = number_of_coef_sets_2;
        number_of_coef_sets[3] = number_of_coef_sets_3;
        number_of_coef_sets[4] = number_of_coef_sets_4;
        number_of_coef_sets[5] = number_of_coef_sets_5;
        number_of_coef_sets[6] = number_of_coef_sets_6;
        number_of_coef_sets[7] = number_of_coef_sets_7;
        number_of_coef_sets[8] = number_of_coef_sets_8;
        number_of_coef_sets[9] = number_of_coef_sets_9;
        number_of_coef_sets[10] = number_of_coef_sets_10;
        number_of_coef_sets[11] = number_of_coef_sets_11;


        logger.info(format("jultime: %s, ephemeris_dates[0]: %s, INTERVAL_DURATION: %s", jultime, ephemeris_dates[0], interval_duration));
        logger.info(format("INTERVAL_DURATION"));
        interval = (int) (Math.floor((jultime - ephemeris_dates[0]) / interval_duration) + 1);
        interval_start_time = (interval - 1) * interval_duration + ephemeris_dates[0];
        subinterval_duration = interval_duration / number_of_coef_sets[heavenlyBody];
        subinterval = (int) (Math.floor((jultime - interval_start_time) / subinterval_duration) + 1);
        numbers_to_skip = (interval - 1) * numbers_per_interval;

        /*
              Starting at the beginning of the coefficient array, skip the first "numbers_to_skip" coefficients.  This
              puts the pointer on the first piece of data in the correct interval.
          */
        pointer = numbers_to_skip + 1;

        /*  Skip the coefficients for the first (heavenlyBody-1) planets  */
        for (j = 1; j <= (heavenlyBody - 1); j++)
            pointer = pointer + 3 * number_of_coef_sets[j] * number_of_coefs[j];

        /*  Skip the next (subinterval - 1)*3*number_of_coefs(heavenlyBody) coefficients  */
        pointer = pointer + (subinterval - 1) * 3 * number_of_coefs[heavenlyBody];

        logger.info(format("interval:%s, interval_start_time: %s, subinterval_duration: %s, subinterval: %s, numbers_to_skip: %s, pointer: %s", interval, interval_start_time, subinterval_duration, subinterval, numbers_to_skip, pointer));
        for (j = 1; j <= 3; j++) {
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++) {
                /*  Read the pointer'th coefficient as the array entry coef[j][k]  */
                coef[j][k] = ephemeris_coefficients[pointer];
                pointer = pointer + 1;
            }
        }

        /*  Calculate the chebyshev time within the subinterval, between -1 and +1  */
        chebyshev_time = 2 * (jultime - ((subinterval - 1) * subinterval_duration + interval_start_time)) / subinterval_duration - 1;

        /*  Calculate the Chebyshev position polynomials   */
        position_poly[1] = 1;
        position_poly[2] = chebyshev_time;
        for (j = 3; j <= number_of_coefs[heavenlyBody]; j++)
            position_poly[j] = 2 * chebyshev_time * position_poly[j - 1] - position_poly[j - 2];

        /*  Calculate the position of the heavenlyBody at jultime  */
        for (j = 1; j <= 3; j++) {
            ephemeris_r[j] = 0;
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++)
                ephemeris_r[j] = ephemeris_r[j] + coef[j][k] * position_poly[k];

            /*  Convert from km to A.U.  */
            ephemeris_r[j] = ephemeris_r[j] / au;
        }

        /*  Calculate the Chebyshev velocity polynomials  */
        velocity_poly[1] = 0;
        velocity_poly[2] = 1;
        velocity_poly[3] = 4 * chebyshev_time;
        for (j = 4; j <= number_of_coefs[heavenlyBody]; j++)
            velocity_poly[j] = 2 * chebyshev_time * velocity_poly[j - 1] + 2 * position_poly[j - 1] - velocity_poly[j - 2];

        /*  Calculate the velocity of the heavenlyBody'th planet  */
        for (j = 1; j <= 3; j++) {
            ephemeris_rprime[j] = 0;
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++)
                ephemeris_rprime[j] = ephemeris_rprime[j] + coef[j][k] * velocity_poly[k];
            /*  The next line accounts for differentiation of the iterative formula with respect to chebyshev time.
            Essentially, if dx/dt = (dx/dct) times (dct/dt), the next line includes the factor (dct/dt) so that the
            units are km/day  */
            ephemeris_rprime[j] = ephemeris_rprime[j] * (2.0 * number_of_coef_sets[heavenlyBody] / interval_duration);

            /*  Convert from km to A.U.  */
            ephemeris_rprime[j] = ephemeris_rprime[j] / au;

        }

    }

    /**********************************************************************/
    /***
     * ALL OF THE BELOW SIMPLY COPIED AND PASTED FROM THE ORIGINAL CODE AS-IS
     ***/
    /**********************************************************************/

    /*  Define ephemeris dates and coefficients as instance variables  */
    Double[] ephemeris_coefficients = new Double[187681];
    double[] ephemeris_dates = new double[3];

    /*
       Length of an A.U., in km
     */
    static final double au = 149597870.691;


    /*
       Chebyshev coefficients for the DE405 ephemeris are contained in the files "ASCPxxxx.txt".  These files are broken
       into intervals of length "INTERVAL_DURATION", in days.
     */
    static int interval_duration = 32;

    /*
       Each interval contains an interval number, length, start and end jultimes, and Chebyshev coefficients.  We keep
       only the coefficients.
     */
    static int numbers_per_interval = 816;

    /*
       For each planet (and the Moon makes 10, and the Sun makes 11), each interval contains several complete sets of
       coefficients, each covering a fraction of the interval duration
     */
    static int number_of_coef_sets_1 = 4;
    static int number_of_coef_sets_2 = 2;
    static int number_of_coef_sets_3 = 2;
    static int number_of_coef_sets_4 = 1;
    static int number_of_coef_sets_5 = 1;
    static int number_of_coef_sets_6 = 1;
    static int number_of_coef_sets_7 = 1;
    static int number_of_coef_sets_8 = 1;
    static int number_of_coef_sets_9 = 1;
    static int number_of_coef_sets_10 = 8;
    static int number_of_coef_sets_11 = 2;

    /*
       Each planet (and the Moon makes 10, and the Sun makes 11) has a different number of Chebyshev coefficients used
       to calculate each component of position and velocity.
     */
    static int number_of_coefs_1 = 14;
    static int number_of_coefs_2 = 10;
    static int number_of_coefs_3 = 13;
    static int number_of_coefs_4 = 11;
    static int number_of_coefs_5 = 8;
    static int number_of_coefs_6 = 7;
    static int number_of_coefs_7 = 6;
    static int number_of_coefs_8 = 6;
    static int number_of_coefs_9 = 6;
    static int number_of_coefs_10 = 13;
    static int number_of_coefs_11 = 11;

}
