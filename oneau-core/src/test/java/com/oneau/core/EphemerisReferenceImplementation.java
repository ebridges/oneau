package com.oneau.core;

import com.oneau.core.util.Constants;
import com.oneau.core.util.HeavenlyBody;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.oneau.core.EphemerisDataFile.INTERVAL_DURATION;
import static com.oneau.core.util.Utility.newDouble;
import static java.lang.String.format;

/**
 * User: EBridges
 * Created: 2010-04-20
 */
public class EphemerisReferenceImplementation {
    private static final Logger logger = Logger.getLogger(EphemerisReferenceImplementation.class);

    public EphemerisReferenceImplementation() {
    }

    public EphemerisReferenceImplementation(Double[] ephemeris_coefficients, Double[] ephemeris_dates) {
        this.ephemerisCoefficients = ephemeris_coefficients;
        this.ephemerisDates = ephemeris_dates;
    }

    public static int getInterval(Double asOf, EphemerisDataFile dataFile) {
        return (int) (Math.floor((asOf - dataFile.getBeginDate()) / INTERVAL_DURATION) + 1);
    }

    public static Double getIntervalStartTime(Double asOf, EphemerisDataFile file) {
        return ((Math.floor((asOf - file.getBeginDate()) / INTERVAL_DURATION) + 1) - 1) * INTERVAL_DURATION + file.getBeginDate();
    }

    public static double getSubintervalDuration(HeavenlyBody body, EphemerisDataFile file) {
        return (file.INTERVAL_DURATION / body.getNumberOfCoefficientSets());
    }

    public static int getSubinterval(double asOf, HeavenlyBody body, EphemerisDataFile file) {
        return (int) (Math.floor((asOf - (((Math.floor((asOf - file.getBeginDate()) / INTERVAL_DURATION) + 1) - 1) * INTERVAL_DURATION + file.getBeginDate())) / (INTERVAL_DURATION / body.getNumberOfCoefficientSets())) + 1);
    }
    
    public Double[] parseEphemerisCoefficients(EphemerisDataFile ephemerisData) throws IOException {
        ephemerisCoefficients = newDouble(ephemerisData.getRecordCount() * 274 * 3);
        int mantissa1;
        int mantissa2;
        int exponent;
        int i=0;

        String line;
        BufferedReader buff = null;
        try {
            buff = readEphemerisData(ephemerisData);

            /* Read each record in the file */
            for (int j = 1; j <= ephemerisData.getRecordCount(); j++) {

                /*  read line 1 and ignore  */
                //noinspection UnusedAssignment
                line = buff.readLine();

                /* read lines 2 through 274 and parse as appropriate */
                for (i = 2; i <= 274; i++) {
                    line = buff.readLine();
                    if (i > 2) {
                        /*  parse first entry  */
                        mantissa1 = Integer.parseInt(line.substring(4, 13));
                        mantissa2 = Integer.parseInt(line.substring(13, 22));
                        exponent = Integer.parseInt(line.substring(24, 26));
                        if (line.substring(23, 24).equals("+"))
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) - 1)] = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
                        else
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) - 1)] = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
                        if (line.substring(1, 2).equals("-"))
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) - 1)] = -ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) - 1)];
                    }
                    if (i > 2) {
                        /*  parse second entry  */
                        mantissa1 = Integer.parseInt(line.substring(30, 39));
                        mantissa2 = Integer.parseInt(line.substring(39, 48));
                        exponent = Integer.parseInt(line.substring(50, 52));
                        if (line.substring(49, 50).equals("+"))
                            ephemerisCoefficients[(j - 1) * 816 + 3 * (i - 2)] = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
                        else
                            ephemerisCoefficients[(j - 1) * 816 + 3 * (i - 2)] = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
                        if (line.substring(27, 28).equals("-"))
                            ephemerisCoefficients[(j - 1) * 816 + 3 * (i - 2)] = -ephemerisCoefficients[(j - 1) * 816 + 3 * (i - 2)];
                    }
                    if (i < 274) {
                        /*  parse third entry  */
                        mantissa1 = Integer.parseInt(line.substring(56, 65));
                        mantissa2 = Integer.parseInt(line.substring(65, 74));
                        exponent = Integer.parseInt(line.substring(76, 78));
                        if (line.substring(75, 76).equals("+"))
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) + 1)] = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
                        else
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) + 1)] = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
                        if (line.substring(53, 54).equals("-"))
                            ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) + 1)] = -ephemerisCoefficients[(j - 1) * 816 + (3 * (i - 2) + 1)];
                    }
                }

                /* read lines 275 through 341 and ignore */
                for (i = 275; i <= 341; i++) {
                    //noinspection UnusedAssignment
                    line = buff.readLine();
                }
            }

        } catch (IOException e) {
            logger.error("Error = " + e.toString());
            throw e;
        } catch (StringIndexOutOfBoundsException e) {
            logger.error("String index out of bounds at i = " + i);
        } finally {
            if(null != buff)
                 buff.close();
        }
        return ephemerisCoefficients;
    }

    private BufferedReader readEphemerisData(EphemerisDataFile data) {
        String filename = format(Constants.EPHMERIS_FILE_ROOT, data.getFileName());
        logger.info(format("Opening file from location [%s].", filename));
        InputStream is = getClass().getResourceAsStream(filename);
        if (null == is) {
            throw new IllegalArgumentException("Unable to open datafile: " + filename + ".");
        }
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }

    public void getPlanetPositionAndVelocity(double jultime, int heavenlyBody, double ephemeris_r[], double ephemeris_rprime[]) throws IOException {
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


        logger.info(format("jultime: %s, ephemerisDates[0]: %s, INTERVAL_DURATION: %s", jultime, ephemerisDates[0], interval_duration));
        interval = (int) (Math.floor((jultime - ephemerisDates[0]) / interval_duration) + 1);
        interval_start_time = (interval - 1) * interval_duration + ephemerisDates[0];
        subinterval_duration = interval_duration / number_of_coef_sets[heavenlyBody];
        subinterval = (int) (Math.floor((jultime - interval_start_time) / subinterval_duration) + 1);
        numbers_to_skip = (interval - 1) * numbers_per_interval;

        /*
              Starting at the beginning of the coefficient array, skip the first "numbers_to_skip" coefficients.  This
              puts the pointer on the first piece of data in the correct interval.
          */
        pointer = numbers_to_skip + 1;

        logger.debug(format("expected: pointer: %d", pointer));

        /*  Skip the coefficients for the first (heavenlyBody-1) planets  */
        for (j = 1; j <= (heavenlyBody - 1); j++) {
            logger.debug(format("e: pointer(%d) + 3 * coeffSets(%d) * coeffCnt(%d)", pointer, number_of_coef_sets[j], number_of_coefs[j]));
            pointer = pointer + 3 * number_of_coef_sets[j] * number_of_coefs[j];
        }

        logger.debug(format("expected: pointer after skipping %d planets: %d", (heavenlyBody - 1), pointer));

        /*  Skip the next (subinterval - 1)*3*number_of_coefs(heavenlyBody) coefficients  */
        pointer = pointer + (subinterval - 1) * 3 * number_of_coefs[heavenlyBody];

        logger.info(format("interval:%s, interval_start_time: %s, subinterval_duration: %s, subinterval: %s, numbers_to_skip: %s, pointer: %s", interval, interval_start_time, subinterval_duration, subinterval, numbers_to_skip, pointer));

        for (j = 1; j <= 3; j++) {
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++) {
                /*  Read the pointer'th coefficient as the array entry coef[j][k]  */
                coef[j][k] = ephemerisCoefficients[pointer];
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
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++){
                logger.debug(format("    e: position[%d](%f) += coefficient[%d][%d](%f) * polynomial[%d](%f)", j, ephemeris_r[j], j, k, coef[j][k], k, position_poly[k]));
                ephemeris_r[j] = ephemeris_r[j] + coef[j][k] * position_poly[k];
            }
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
            for (k = 1; k <= number_of_coefs[heavenlyBody]; k++){
                logger.debug(format("    e: velocity[%d](%f) += coefficient[%d][%d](%f) * polynomial[%d](%f)", j, ephemeris_rprime[j], j, k, coef[j][k], k, velocity_poly[k]));
                ephemeris_rprime[j] = ephemeris_rprime[j] + coef[j][k] * velocity_poly[k];
            }
            /*  The next line accounts for differentiation of the iterative formula with respect to chebyshev time.
            Essentially, if dx/dt = (dx/dct) times (dct/dt), the next line includes the factor (dct/dt) so that the
            units are km/day  */
            if(logger.isDebugEnabled()) {
                logger.debug(format("   e: velocity[%d](%f) *= (2.0 * NumCoefficientSets(%d) / INTERVAL_DURATION(%d)", j, ephemeris_rprime[j], number_of_coef_sets[heavenlyBody], interval_duration));
            }
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
    Double[] ephemerisCoefficients = newDouble(187681);
    Double[] ephemerisDates = newDouble(3);

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
