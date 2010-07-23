package com.oneau.core;

import com.oneau.core.util.Constants;
import com.oneau.core.util.Converter;
import com.oneau.core.util.ConverterFactory;
import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import com.oneau.core.util.Utility;
import com.oneau.parser.ephemeris.AscpFileParser;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.HeaderParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oneau.core.util.HeavenlyBody.EARTH;
import static com.oneau.core.util.HeavenlyBody.MOON;
import static com.oneau.core.util.Utility.contains;
import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

/**
 * This class contains the methods necessary to parse the JPL DE405 ephemeris files (text versions), and compute
 * the position and velocity of the planets, Moon, and Sun.
 * <p/>
 * The input is the julian date (jultime) for which the ephemeris is needed.  Note that only julian dates from
 * 2414992.5 to 2524624.5 are supported.  This input must be specified in the "main" method, which contains the
 * call to "calculatePlanetaryEphemeris".
 * <p/>
 * GENERAL IDEA:
 * The "parseEphemerisCoefficients" method reads the ephemeris file corresponding to the input julian day, and
 * stores the ephemeris coefficients needed to calculate planetary positions and velocities in the array
 * "ephemeris_coefficients". The "getPlanetPositionAndVelocity" method calls "parseEphemerisCoefficients" if
 * needed, then calculates the position and velocity of the specified planet.
 * The "calculatePlanetaryEphemeris" method calls "getPlanetPositionAndVelocity" for each planet, and resolves
 * the position and velocity of the Earth/Moon barycenter and geocentric Moon into the position and velocity of
 * the Earth and Moon.
 * Since the "ephemeris_coefficients" array is declared as an instance variable, its contents will remain intact,
 * should this code be modified to call "calculatePlanetaryEphemeris" more than once.  As a result, assuming the
 * julian date of the subsequent call fell within the same 20-year file as the initial call, there would be no
 * need to reread the ephemeris file; this would save on i/o time.
 * The outputs are the arrays "position" and "velocity", also declared as instance variables.
 * Several key constants and variables follow.  As noted, they are configured for DE405; however, they could be
 * adjusted to use the DE200 ephemeris, whose format is quite similar.
 */
public class Ephemeris {
    private static final Logger logger = Logger.getLogger(Ephemeris.class);
    private final Map<EphemerisDataFile, EphemerisData> DATAFILE_CACHE = new HashMap<EphemerisDataFile, EphemerisData>();

    public Ephemeris() {
    }

    public void loadData(String... files) throws IOException {
        for (String file : files) {
            logger.trace(format("pre-loading file [%s]", file));
            EphemerisDataFile dataFile = EphemerisDataFile.lookupByName(file);
            if (!DATAFILE_CACHE.containsKey(dataFile)) {
                logger.info(format("precaching dataFile [%s]", file));
                DATAFILE_CACHE.put(dataFile, new EphemerisData(dataFile));
                logger.info(format("dataFile [%s] successfully cached.", file));
            }
        }
    }

    /**
     * Procedure to calculate the position and velocity at jultime of the major planets.
     * Note that the planets are enumerated as follows:
     * Mercury = 1,
     * Venus = 2,
     * Earth-Moon barycenter = 3,
     * Mars = 4,
     * ... ,
     * Pluto = 9,
     * Geocentric Moon = 10,
     * Sun = 11.
     *
     * @param jultime        Date for which the position &amp; velocity should be calculated.
     * @param heavenlyBodies Planetary bodies for which to calculate position &amp; velocity.
     * @return Map<HeavenlyBody, PositionAndVelocity> Results of calculations of position and velocity for given heavenly bodies.
     * @throws java.io.IOException Thrown when I/O error.
     */
    public Map<HeavenlyBody, PositionAndVelocity> calculatePlanetaryEphemeris(double jultime, HeavenlyBody... heavenlyBodies) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("calculatePlanetaryEphemeris() called.");
        }

        if (isEmpty(heavenlyBodies)) {
            heavenlyBodies = HeavenlyBody.values();
        } else if (contains(heavenlyBodies, EARTH) && !contains(heavenlyBodies, MOON)) {
            HeavenlyBody[] t = new HeavenlyBody[heavenlyBodies.length + 1];
            int i = 0;
            for (; i < heavenlyBodies.length; i++) {
                t[i] = heavenlyBodies[i];
            }
            t[i] = MOON;
            heavenlyBodies = t;
        }

        Map<HeavenlyBody, PositionAndVelocity> results = new HashMap<HeavenlyBody, PositionAndVelocity>();

        //  Get the ephemeris positions and velocities of each major planet
        for (HeavenlyBody body : heavenlyBodies) {
            PositionAndVelocity pv = getPlanetPositionAndVelocity(jultime, body);
            results.put(pv.getBody(), pv);
        }

        /*
         * The positions and velocities of the Earth and Moon are found indirectly: We already have the pos/vel
         * of the Earth-Moon barycenter (i = 3). We have also calculated position(10,j), a geocentric vector from
         * the Earth to the Moon.  Using the ratio of masses, we get vectors from the Earth-Moon barycenter to the
         * Moon and to the Earth.
         */
        PositionAndVelocity earth = results.get(EARTH);
        PositionAndVelocity moon = results.get(MOON);
        if (null != earth && null != moon) {
            for (int j = 0; j < 3; j++) {
                // EARTH = EARTH - (MOON/1+EMrat)
                earth.getPosition()[j] = earth.getPosition()[j] - moon.getPosition()[j] / (1 + Constants.EMrat);
                // MOON = EARTH + MOON
                moon.getPosition()[j] = earth.getPosition()[j] + moon.getPosition()[j];

                // EARTH = EARTH - (MOON/1+EMrat)
                earth.getVelocity()[j] = earth.getVelocity()[j] - moon.getVelocity()[j] / (1 + Constants.EMrat);
                // MOON = EARTH + MOON
                moon.getVelocity()[j] = earth.getVelocity()[j] + moon.getVelocity()[j];
            }
        }

        return results;
    }

    /**
     * Procedure to calculate the position and velocity of planet i, subject to the JPL DE405 ephemeris.  The
     * positions and velocities are calculated using Chebyshev polynomials, the coefficients of which are stored
     * in the files "ASCPxxxx.txt".
     * <p/>
     * The general idea is as follows:  First, check to be sure the proper ephemeris coefficients (corresponding to
     * jultime) are available.  Then read the coefficients corresponding to jultime, and calculate the positions and
     * velocities of the planet.
     *
     * @param jultime         Time at which position is to be calculated.
     * @param heavenlyBody    Body to calculate position of.
     * @param resultConverter One or more result transformers.
     * @return PositionAndVelocity Position and velocity of the given body at the given time.
     * @throws java.io.IOException Thrown when I/O error occurs when reading data file.
     */
    PositionAndVelocity getPlanetPositionAndVelocity(double jultime, HeavenlyBody heavenlyBody, Converter... resultConverter) throws IOException {
        /*
            Begin by determining whether the current ephemeris coefficients are appropriate for jultime, or if we need
            to load a new set.
          */
        EphemerisDataFile dataFile = EphemerisDataFile.lookupByDate(jultime);
        if (!DATAFILE_CACHE.containsKey(dataFile)) {
            DATAFILE_CACHE.put(dataFile, new EphemerisData(dataFile));
        }
        EphemerisData data = DATAFILE_CACHE.get(dataFile);
        EphemerisDataView dataView = data.getDataForBody(heavenlyBody, jultime);
//        EphemerisDataView dataView = getViewForDate(heavenlyBody, jultime);

        Converter c;
        if (null != resultConverter && resultConverter.length > 0) {
            c = resultConverter[0];
        } else {
            c = ConverterFactory.getConverter(Converter.TYPE.AU);
        }

        Double[] position = calculatePosition(dataView, c);
        Double[] velocity = calculateVelocity(dataView, c);

        return new PositionAndVelocity(jultime, heavenlyBody, position, velocity);
    }

    private EphemerisDataView getViewForDate(HeavenlyBody heavenlyBody, double jultime) {
        HeaderParser headerParser = new HeaderParser(HeaderParser.HEADER_405);
        Header header = null;
        EphemerisDataFile file = EphemerisDataFile.lookupByDate(jultime);
        EphemerisDataViewObservationWriter viewWriter = new EphemerisDataViewObservationWriter(file, heavenlyBody, jultime);
        try {
            header = headerParser.readHeader();

            logger.info("found file: "+file.getFileName()+" for date: " + jultime);
            AscpFileParser coeffParser = new AscpFileParser(header, file.getFileName());
            coeffParser.readObservationsFromFile(viewWriter);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return viewWriter;
    }

    private Double[] calculateVelocity(EphemerisDataView ephemerisData, Converter resultConverter) {
        Double[] velocity = newDouble(3);
        double chebyshevTime = ephemerisData.getChebyshevTime();
        int coefficientCount = ephemerisData.getBody().getNumberOfChebyshevCoefficients();
        List<Double> coefficients = ephemerisData.getCoefficients();

        /*  Calculate the Chebyshev velocity polynomials  */
        double[] polynomial = createVelocityPolynomial(coefficientCount, chebyshevTime);

        /*
         * Calculate the velocity of the planet at jultime. The coefficients returned from the dataview is a
         * 2-d matrix of coefficients flattened to a 1-d array. Therefore the indexing is a running sum of
         * the index "i".
         */
        int i = 0;
        for (int x = 0; x < 3; x++) {
            velocity[x] = 0.0;
            for (int y = 0; y < coefficientCount; y++) {
                if (logger.isDebugEnabled()) {
                    logger.debug(format("    a: velocity[%d](%f) += coefficient[%d](%f) * polynomial[%d](%f)", x, velocity[x], i, coefficients.get(i), y, polynomial[y]));
                }
                velocity[x] += coefficients.get(i++) * polynomial[y];
            }
            /*
             * The next line accounts for differentiation of the iterative formula with respect to chebyshev time.
             * Essentially, if dx/dt = (dx/dct) times (dct/dt), the next line includes the factor (dct/dt) so that
             * the units are km/day
             */
            if (logger.isDebugEnabled()) {
                logger.debug(format("   a: velocity[%d](%f) *= (2.0 * NumCoefficientSets(%d) / INTERVAL_DURATION(%d)", x, velocity[x], ephemerisData.getBody().getNumberOfCoefficientSets(), EphemerisDataFile.INTERVAL_DURATION));
            }
            velocity[x] *= (2.0 * ephemerisData.getBody().getNumberOfCoefficientSets() / EphemerisDataFile.INTERVAL_DURATION);

            if (logger.isDebugEnabled()) {
                logger.debug(format("velocity[%d]=%fkm, %fau", x, velocity[x], resultConverter.convert(velocity[x])));
            }
            velocity[x] = resultConverter.convert(velocity[x]);
        }
        return velocity;
    }

    private Double[] calculatePosition(EphemerisDataView ephemerisData, Converter resultConverter) {
        /*  Calculate the Chebyshev position polynomials   */
        /* Chebyshev Polynomials of the first kind:
            Tn+1(x) = 2 x Tn(x) - Tn-1(x)
            Using this equation, we see....
            T2( x ) = 2x (x) - 1 = 2x^2 - 1
            T3( x ) = 2x (2x^2 - 1) - x = 4x^3 - 3x
            T4( x ) = 2x (4x^3 - 3x) - (2x^2 - 1) = 8x^4 - 8x^2 + 1
         */
        Double[] position = newDouble(3);
        double chebyshevTime = ephemerisData.getChebyshevTime();
        int coefficientCount = ephemerisData.getBody().getNumberOfChebyshevCoefficients();
        List<Double> coefficients = ephemerisData.getCoefficients();

        double[] polynomial = createPositionPolynomial(coefficientCount, chebyshevTime);

        /*
         * Calculate the position of the planet at jultime. The coefficients returned from the dataview is a
         * 2-d matrix of coefficients flattened to a 1-d array. Therefore the indexing is a running sum of
         * the index "i".
         */
        int i = 0;
        for (int x = 0; x < 3; x++) {
            position[x] = 0.0;
            for (int y = 0; y < coefficientCount; y++) {
                if (logger.isDebugEnabled()) {
                    logger.debug(format("    a: position[%d](%f) += coefficient[%d](%f) * polynomial[%d](%f)", x, position[x], i, coefficients.get(i), y, polynomial[y]));
                }
                position[x] += coefficients.get(i++) * polynomial[y];
            }

            if (logger.isDebugEnabled()) {
                logger.debug(format("position[%d]=%fkm, %fau", x, position[x], resultConverter.convert(position[x])));
            }
            position[x] = resultConverter.convert(position[x]);
        }
        return position;
    }

    private double[] createVelocityPolynomial(int coefficientCount, double chebyshevTime) {
        double[] positionPolynomial = createPositionPolynomial(coefficientCount, chebyshevTime);
        double[] polynomial = new double[coefficientCount];
        polynomial[0] = 0;
        polynomial[1] = 1;
        polynomial[2] = 4 * chebyshevTime;
        for (int j = 3; j < coefficientCount; j++)
            polynomial[j] = 2 * chebyshevTime * polynomial[j - 1] + 2 * positionPolynomial[j - 1] - polynomial[j - 2];

        return polynomial;
    }

    private double[] createPositionPolynomial(int coefficientCount, double chebyshevTime) {
        double[] polynomial = new double[coefficientCount];
        polynomial[0] = 1;
        polynomial[1] = chebyshevTime;
        for (int j = 2; j < coefficientCount; j++)
            polynomial[j] = 2 * chebyshevTime * polynomial[j - 1] - polynomial[j - 2];
        return polynomial;
    }

    private Double[] newDouble(int i) {
        Double[] d = new Double[i];
        for (int ii = 0; ii < d.length; ii++)
            d[ii] = 0.0;
        return d;
    }

    public static void main(String args[]) throws Exception {
        /* USER MUST SPECIFY jultime HERE.  Example value is 2451545.0 */
        double jultime = 2451545.0;

        long start = System.currentTimeMillis();
        Ephemeris testBody = new Ephemeris();

        /*
            This is the call to "calculatePlanetaryEphemeris", which will put planetary positions into the array "position",
            and planetary velocities into the array "velocity".
          */
        Map<HeavenlyBody, PositionAndVelocity> results = testBody.calculatePlanetaryEphemeris(jultime);

        long end = System.currentTimeMillis();
        logger.info("run duration: " + (end - start) + "ms");

        /*  The following simply sends the output to the screen */
        for (Map.Entry<HeavenlyBody, PositionAndVelocity> r : results.entrySet()) {
            PositionAndVelocity pav = r.getValue();
            logger.info(r.getKey().getName());
            logger.info("    Position: " + Utility.toString(pav.getPosition()));
            logger.info("    Velocity: " + Utility.toString(pav.getVelocity()));
        }
    }
}

