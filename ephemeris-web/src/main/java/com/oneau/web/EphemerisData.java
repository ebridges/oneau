package com.oneau.web;

import com.oneau.web.util.HeavenlyBody;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.oneau.web.util.Utility.newDouble;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * User: EBridges
 * Created: 2010-04-12
 */
public class EphemerisData {
    private static final Logger logger = Logger.getLogger(EphemerisData.class);

    private EphemerisDataFile dataFile;
    private Double[] ephemerisCoefficients;

    public EphemerisData(EphemerisDataFile dataFile) throws IOException {
        this.dataFile = dataFile;
        this.ephemerisCoefficients = parseEphemerisCoefficients(this.dataFile);
    }

    public EphemerisDataView getDataForBody(HeavenlyBody body, Double asOf) {
        if (null == body) {
            throw new IllegalArgumentException("body cannot be null");
        }
        return new EphemerisDataView(body, asOf);
    }

    public EphemerisDataFile getDataFile() {
        return dataFile;
    }

    public Double[] getEphemerisCoefficients() {
        return ephemerisCoefficients;
    }

    /**
     * Procedure to read the DE405 ephemeris file corresponding to jultime.  Returns the Chebyshev coefficients for
     * Mercury, Venus, Earth-Moon, Mars, Jupiter, Saturn, Uranus, Neptune, Pluto, Geocentric Moon, and Sun.
     * <p/>
     * Note that the DE405 ephemeris files should on the same classpath as this class.
     *
     * @param dataFile Ephemeris data file relative to date range the application is searching for.
     * @return double[] Chebyshev coefficients parsed from the given file.
     * @throws java.io.IOException when an i/o error occurs.
     */
    private Double[] parseEphemerisCoefficients(EphemerisDataFile dataFile) throws IOException {
        Double[] coefficients = newDouble(dataFile.getRecordCount() * 274 * 3);
        int mantissa1;
        int mantissa2;
        int exponent;
        int coefficient = 0;

        String line;
        BufferedReader buff = null;
        try {
            buff = readEphemerisData();

            /*
             * Read each record in the file -- each record has 274 lines of 3 coefficients per line (816 total),
             * and the first two coefficients per record are skipped.
             */
            for (int record = 1; record <= dataFile.getRecordCount(); record++) {
                buff.readLine(); // skip the file's header line

                /*
                 * read coefficient 2 through 274 and parse as appropriate
                 */
                for (coefficient = 2; coefficient <= 274; coefficient++) {
                    line = buff.readLine();
                    if (coefficient > 2) {
                        //  parse first entry
                        mantissa1 = Integer.parseInt(line.substring(4, 13));
                        mantissa2 = Integer.parseInt(line.substring(13, 22));
                        exponent = Integer.parseInt(line.substring(24, 26));
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("1st entry (record:%d,coefficient:%d): [mantissa1:%d] [mantissa2:%d] [exponent:%d]", record, coefficient, mantissa1, mantissa2, exponent));
                        }
                        int idx = (record - 1) * 816 + (3 * (coefficient - 2) - 1);
                        coefficients[idx] = buildCoefficient(
                                mantissa1,
                                mantissa2,
                                exponent,
                                line.substring(1, 2).equals("-"),
                                line.substring(23, 24).equals("-")
                        );
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("  coefficient[%s]: %s", idx, coefficients[idx]));
                        }
                    }
                    if (coefficient > 2) {
                        //  parse second entry
                        mantissa1 = Integer.parseInt(line.substring(30, 39));
                        mantissa2 = Integer.parseInt(line.substring(39, 48));
                        exponent = Integer.parseInt(line.substring(50, 52));
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("2nd entry (record:%d,coefficient:%d): [mantissa1:%d] [mantissa2:%d] [exponent:%d]", record, coefficient, mantissa1, mantissa2, exponent));
                        }
                        int idx = (record - 1) * 816 + 3 * (coefficient - 2);
                        coefficients[idx] = buildCoefficient(
                                mantissa1,
                                mantissa2,
                                exponent,
                                line.substring(27, 28).equals("-"),
                                line.substring(49, 50).equals("-")
                        );
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("  coefficient[%s]: %s", idx, coefficients[idx]));
                        }
                    }
                    if (coefficient < 274) {
                        //  parse third entry
                        mantissa1 = Integer.parseInt(line.substring(56, 65));
                        mantissa2 = Integer.parseInt(line.substring(65, 74));
                        exponent = Integer.parseInt(line.substring(76, 78));
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("3rd entry (record:%d,coefficient:%d): [mantissa1:%d] [mantissa2:%d] [exponent:%d]", record, coefficient, mantissa1, mantissa2, exponent));
                        }
                        int idx = (record - 1) * 816 + (3 * (coefficient - 2) + 1);
                        coefficients[idx] = buildCoefficient(
                                mantissa1,
                                mantissa2,
                                exponent,
                                line.substring(53, 54).equals("-"),
                                line.substring(75, 76).equals("-")
                        );
                        if (logger.isTraceEnabled()) {
                            logger.trace(format("  coefficient[%s]: %s", idx, coefficients[idx]));
                        }
                    }
                }

                /*
                 * read lines 275 through 341 and ignore
                 */
                for (coefficient = 275; coefficient <= 341; coefficient++)
                    buff.readLine();
            }

        } catch (IOException e) {
            logger.error("Error = " + e.toString());
            throw e;
        } catch (StringIndexOutOfBoundsException e) {
            logger.error("String index out of bounds at coefficient = " + coefficient);
            throw e;
        } finally {
            if (null != buff)
                buff.close();
        }
        return coefficients;
    }

    private double buildCoefficient(int mantissa1, int mantissa2, int exponent, boolean isCoefficientNegative, boolean isExponentNegative) {
        double coefficient;
        if (isExponentNegative) {
            coefficient = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
        } else {
            coefficient = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
        }
        return (isCoefficientNegative ? -coefficient : +coefficient);
    }

    private BufferedReader readEphemerisData() {
        String filename = format("/%s", dataFile.getFileName());
        logger.info(format("Opening file from location [%s].", filename));
        InputStream is = getClass().getResourceAsStream(filename);
        if (null == is) {
            throw new IllegalArgumentException("Unable to open datafile: " + filename + ".");
        }
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }

    class EphemerisDataView {
        private final HeavenlyBody body;
        private final Double asOf;

        public EphemerisDataView(HeavenlyBody body, Double julianEphemerisDate) {
            this.body = body;
            this.asOf = julianEphemerisDate;
        }

        List<Double> getCoefficients() {
            List<Double> coefficients = asList(ephemerisCoefficients);
            return unmodifiableList(coefficients.subList(
                    getViewBegin(),
                    getViewEnd()
            ));
        }

        public HeavenlyBody getBody() {
            return body;
        }

        public Double getAsOf() {
            return asOf;
        }

        public double getChebyshevTime() {
            return 2 * (asOf - ((dataFile.getSubinterval(body, asOf) - 1) * dataFile.getSubintervalDuration(body) + dataFile.getIntervalStartTime(asOf))) / dataFile.getSubintervalDuration(body) - 1;
        }

        private int getViewBegin() {
            int interval = dataFile.getInterval(asOf);
            int subinterval = dataFile.getSubinterval(body, asOf);
            int numbersToSkip = (interval - 1) * EphemerisDataFile.NUMBERS_PER_INTERVAL;

            /*
             * Starting at the beginning of the coefficient array, skip the first "numbersToSkip"
             * coefficients.  This puts the pointer on the first piece of data in the correct interval.
             */
            int pointer = numbersToSkip + 1;

            // Skip the coefficients for the planets ahead of this one
            for (HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
                if (b == body) {
                    break;
                }
                pointer = pointer + 3 * b.getNumberOfCoefficientSets() * b.getNumberOfChebyshevCoefficients();
            }

            // Skip the next (subinterval - 1)*3*number_of_coefs(heavenlyBody) coefficients
            pointer += (subinterval - 1) * 3 * body.getNumberOfChebyshevCoefficients();

            if (logger.isDebugEnabled()) {
                logger.debug(format("interval: %d, subinterval: %d, numbersToSkip: %d, pointer: %d", interval, subinterval, numbersToSkip, pointer));
            }

            return pointer;
        }

        private int getViewEnd() {
            return getViewBegin() + (3 * body.getNumberOfChebyshevCoefficients());
        }
    }
}
