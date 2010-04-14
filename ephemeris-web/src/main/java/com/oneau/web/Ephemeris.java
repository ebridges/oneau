package com.oneau.web;

import static com.oneau.web.util.HeavenlyBody.MOON;
import static com.oneau.web.util.HeavenlyBody.EARTH;
import static java.lang.String.format;

import com.oneau.web.util.Constants;
import com.oneau.web.util.HeavenlyBody;
import com.oneau.web.util.Utility;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains the methods necessary to parse the JPL DE405 ephemeris files (text versions), and compute
 * the position and velocity of the planets, Moon, and Sun.
 *
 * The input is the julian date (jultime) for which the ephemeris is needed.  Note that only julian dates from
 * 2414992.5 to 2524624.5 are supported.  This input must be specified in the "main" method, which contains the
 * call to "calculatePlanetaryEphemeris".
 *
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
    private final Map<EphemerisDataFile,EphemerisData> DATAFILE_CACHE = new HashMap<EphemerisDataFile,EphemerisData>();

    public Ephemeris() {
    }

    public void loadData(String ... files) throws IOException {
        for(String file : files) {
            logger.trace(format("pre-loading file [%s]", file));
            EphemerisDataFile dataFile = EphemerisDataFile.lookupByName(file);
            if(!DATAFILE_CACHE.containsKey(dataFile)){
                logger.info(format("precaching dataFile [%s]", file));
                DATAFILE_CACHE.put(dataFile, new EphemerisData(dataFile));
            }
        }
    }

    /**
     * Procedure to calculate the position and velocity at jultime of the major planets.
     * Note that the planets are enumerated as follows:
     *     Mercury = 1,
     *     Venus = 2,
     *     Earth-Moon barycenter = 3,
     *     Mars = 4,
     *     ... ,
     *     Pluto = 9,
     *     Geocentric Moon = 10,
     *     Sun = 11.
     *
     * @param jultime Date for which the position &amp; velocity should be calculated.
     * @param heavenlyBodies Planetary bodies for which to calculate position &amp; velocity.
     * @throws java.io.IOException Thrown when I/O error.
     * @return Map<HeavenlyBody, PositionAndVelocity> Results of calculations of position and velocity for given heavenly bodies.
     */
    public Map<HeavenlyBody, PositionAndVelocity> calculatePlanetaryEphemeris(double jultime, HeavenlyBody ... heavenlyBodies) throws IOException {
        Map<HeavenlyBody, PositionAndVelocity> results = new HashMap<HeavenlyBody, PositionAndVelocity>();

        //  Get the ephemeris positions and velocities of each major planet
        for(HeavenlyBody body : heavenlyBodies) {
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
        if(null != earth && null != moon) {
            for (int j = 0; j < 3; j++) {
                // EARTH = EARTH - (MOON/1+EMRAT)
                earth.getPosition()[j] = earth.getPosition()[j] - moon.getPosition()[j] / (1+ Constants.EMRAT);
                // MOON = EARTH + MOON
                moon.getPosition()[j] = earth.getPosition()[j] + moon.getPosition()[j];

                // EARTH = EARTH - (MOON/1+EMRAT)
                earth.getVelocity()[j] = earth.getVelocity()[j] - moon.getVelocity()[j] / (1+ Constants.EMRAT);
                // MOON = EARTH + MOON
                moon.getVelocity()[j] = earth.getVelocity()[j] + moon.getVelocity()[j];
            }
        }

        return results;
    }

    /**
     *  Procedure to calculate the position and velocity of planet i, subject to the JPL DE405 ephemeris.  The
     * positions and velocities are calculated using Chebyshev polynomials, the coefficients of which are stored
     * in the files "ASCPxxxx.txt".
     *
     * The general idea is as follows:  First, check to be sure the proper ephemeris coefficients (corresponding to
     * jultime) are available.  Then read the coefficients corresponding to jultime, and calculate the positions and
     * velocities of the planet.
     *
     * @param jultime Time at which position is to be calculated.
     * @param heavenlyBody Body to calculate position of.
     * @return PositionAndVelocity Position and velocity of the given body at the given time.
     * @throws java.io.IOException Thrown when I/O error occurs when reading data file.
     */
     PositionAndVelocity getPlanetPositionAndVelocity(double jultime, HeavenlyBody heavenlyBody) throws IOException {
        /*
            Begin by determining whether the current ephemeris coefficients are appropriate for jultime, or if we need
            to load a new set.
          */
        EphemerisDataFile dataFile = EphemerisDataFile.lookupByDate(jultime);
        if(!DATAFILE_CACHE.containsKey(dataFile)){
            DATAFILE_CACHE.put(dataFile, new EphemerisData(dataFile));
        }
        EphemerisData data = DATAFILE_CACHE.get(dataFile);
        EphemerisData.EphemerisDataView dataView = data.getDataForBody(heavenlyBody, jultime);

        Double[] position = calculatePosition(dataView);
        Double[] velocity = calculateVelocity(dataView);

        return new PositionAndVelocity(heavenlyBody, position, velocity);
    }

    private Double[] calculateVelocity(EphemerisData.EphemerisDataView ephemerisData) {
        Double[] velocity = new Double[3];
        double chebyshevTime = ephemerisData.getChebyshevTime();
        int coefficientCount = ephemerisData.getBody().getNumberOfChebyshevCoefficients();
        double[] polynomial = new double[coefficientCount];
        List<Double> coefficients = ephemerisData.getCoefficients();

        /*  Calculate the Chebyshev velocity polynomials  */
        polynomial[0] = 0;
        polynomial[1] = 1;
        polynomial[2] = 4 * chebyshevTime;
        for (int j = 3; j < coefficientCount; j++)
            polynomial[j] = 2 * chebyshevTime * polynomial[j - 1] + 2 * polynomial[j - 1] - polynomial[j - 2];

        /*  Calculate the velocity of the heavenlyBody'th planet  */
        for (int j = 0; j < 3; j++) {
            velocity[j] = 0.0;
            for (int k = 0; k < coefficientCount; k++)
                velocity[j] += coefficients.get(k) * polynomial[k];
            /*
             * The next line accounts for differentiation of the iterative formula with respect to chebyshev time.
             * Essentially, if dx/dt = (dx/dct) times (dct/dt), the next line includes the factor (dct/dt) so that
             * the units are km/day
             */
            velocity[j] *= (2.0 * ephemerisData.getBody().getNumberOfCoefficientSets() / EphemerisDataFile.INTERVAL_DURATION);

            /*  Convert from km to A.U.  */
            velocity[j] /= Constants.AU;
        }
        return velocity;
    }

    private Double[] calculatePosition(EphemerisData.EphemerisDataView ephemerisData) {
        /*  Calculate the Chebyshev position polynomials   */
        /* Chebyshev Polynomials of the first kind:
            Tn+1(x) = 2 x Tn(x) - Tn-1(x)
            Using this equation, we see....
            T2( x ) = 2x (x) - 1 = 2x^2 - 1
            T3( x ) = 2x (2x^2 - 1) - x = 4x^3 - 3x
            T4( x ) = 2x (4x^3 - 3x) - (2x^2 - 1) = 8x^4 - 8x^2 + 1
         */
        Double[] position = new Double[3];
        double chebyshevTime = ephemerisData.getChebyshevTime();
        int coefficientCount = ephemerisData.getBody().getNumberOfChebyshevCoefficients();
        List<Double> coefficients = ephemerisData.getCoefficients();

        double[] polynomial = new double[coefficientCount];
        polynomial[0] = 1;
        polynomial[1] = chebyshevTime;
        for (int j = 2; j < coefficientCount; j++)
            polynomial[j] = 2 * chebyshevTime * polynomial[j - 1] - polynomial[j - 2];

        /*  Calculate the position of the planet at jultime  */
        for (int j = 0; j < 3; j++) {
            position[j] = 0.0;
            for (int k = 0; k < coefficientCount; k++){
                position[j] += coefficients.get(k) * polynomial[k];
            }

            /*  Convert from km to A.U.  */
            position[j] /= Constants.AU;
        }
        return position;
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
        logger.info("run duration: "+(end-start)+"ms");

        /*  The following simply sends the output to the screen */
        for(Map.Entry<HeavenlyBody, PositionAndVelocity> r : results.entrySet()) {
            PositionAndVelocity pav = r.getValue();
            logger.info(r.getKey().getName());
            logger.info("    Position: "+ Utility.toString(pav.getPosition()));
            logger.info("    Velocity: "+ Utility.toString(pav.getVelocity()));
        }
    }
}

class PositionAndVelocity {
    private HeavenlyBody body;
    private Double[] position;
    private Double[] velocity;

    public PositionAndVelocity(HeavenlyBody body, Double[] position, Double[] velocity) {
        this.body = body;
        this.position = position;
        this.velocity = velocity;
    }

    public HeavenlyBody getBody() {
        return body;
    }

    public Double[] getPosition() {
        return position;
    }

    public Double[] getVelocity() {
        return velocity;
    }
}

