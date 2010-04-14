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
        Double[] coefficients = this.parseEphemerisCoefficients(DATA_FILE);
        assertNotNull(coefficients);
        assertNotNull(data.getEphemerisCoefficients());
        assertArraysEqual(coefficients, data.getEphemerisCoefficients());
    }

    private Double[] parseEphemerisCoefficients(EphemerisDataFile ephemerisData) throws IOException {
        Double[] ephemerisCoefficients = new Double[ephemerisData.getRecordCount() * 274 * 3];
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
                for (i = 275; i <= 341; i++)
                    line = buff.readLine();
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
        String filename = format("/%s", data.getFileName());
        logger.info(format("Opening file from location [%s].", filename));
        InputStream is = getClass().getResourceAsStream(filename);
        if (null == is) {
            throw new IllegalArgumentException("Unable to open datafile: " + filename + ".");
        }
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }
}
