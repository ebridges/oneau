package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;
import static com.oneau.core.util.Utility.parseCoefficient;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class ObservationParser {
    private static final Logger logger = Logger.getLogger(ObservationParser.class.getName());
    private Header header;
    private String filename;
    private Integer observationNum;
    private Integer coefficientCount;

    public ObservationParser(Header header, String filename, Integer observationNum, Integer coefficientCount) {
        this.header = header;
        this.filename = filename;
        this.observationNum = observationNum;
        this.coefficientCount = coefficientCount;
    }

    public Observation parseObservation(BufferedReader reader) throws IOException {
        Observation o = new Observation(filename, observationNum);
        
        List<Double> coefficients = readAllCoefficients(reader);

        if(logger.isLoggable(Level.INFO)){
            logger.info("Read "+coefficients.size()+" coefficients.");
        }

        o.setBeginEndDates(new Range<Double>(
            coefficients.get(0),
            coefficients.get(1)
        ));

        o.setCoefficients(
            gatherPlanetaryCoefficients(header, coefficients)
        );

        return o;
    }

    private List<Double> readAllCoefficients(BufferedReader reader) throws IOException {
        // add 2 to account for begin & end dates at beginning of list of coefficients
        int numCount = coefficientCount+2;
        int lines = numCount/3;
        int lineCount = 0;

        List<Double> coefficients = new ArrayList<Double>(numCount);

        String line = null;

        while( lineCount < lines ) {
            line = reader.readLine();

            if(isEmpty(line)) {
                throw new IllegalArgumentException("premature end of observation!");
            }

            logger.finest(line);

            String[] fields = line.trim().split("\\s+");
            assert fields.length == 3;

            for(String f : fields) {
                Double d = parseCoefficient(f);
                coefficients.add(d);
            }

            lineCount++;
        }

        if(coefficients.size() != numCount) {
            throw new IllegalStateException(format("observation #%d in file %s had incorrect number of coefficients. Expected %d, but was %d", observationNum, filename, coefficientCount, coefficients.size()));
        }
        
        return coefficients;
    }

    /**
     * Takes the overall list of all coefficients, extracts a reference to the subset that is related to the
     * current body, and then divides those into a list of n-dim coordinates.  ('n' is 3 in all cases except for
     * nutations, which is 2 -- see HeavenlyBody).
     *
     * @param header The header for the ephemeris that this set of coefficients is part of.
     * @param coefficients The list of all coefficients for this file.
     * @return The list of coefficients divided among the different bodies being observed.
     */
    private Map<HeavenlyBody, List<Double>> gatherPlanetaryCoefficients(final Header header, final List<Double> coefficients) {
        Map<HeavenlyBody, List<Double>> c = new HashMap<HeavenlyBody, List<Double>>();

//        int expectedNum = coefficients.size();
//        int actualNum = 0;
        
        for(HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
            CoefficientInfo ci = header.getCoeffInfo().get(b);

            final int start = ci.getAdjustedIndex();
            final int numCoefficients =  ci.getCoeffSets() * ci.getCoeffCount() * b.getDimensions();
//            final int numCoefficients = ci.getCoeffCount() * b.getDimensions();
            final int end = ci.getAdjustedIndex() + numCoefficients;
//            final int dims = b.getDimensions();

            // there's nothing special about this method, it's just to ensure
            // that rounding up when needed happens
//            int numCoordinates = deriveCoordListSize(numCoefficients, dims);

            //List<Coordinate> co = new ArrayList<Coordinate>(numCoordinates);

            // this is the subset related to this body
            List<Double> subset = coefficients.subList(start, end);
//            actualNum += subset.size();
            /*
            for(int i=start; i<end; i++) {
                Double x = subset.get(i++);
                Double y = subset.get(i++);
                Double z = subset.get(i++);
                Coordinate coo = new Coordinate(x, y, z);
                co.add(coo);
            }
            */

            /*
            // this steps through
            int currIndex = start;
            while(currIndex < end) {
                List<Double> coordinates = subset.subList(currIndex, (currIndex+dims-1));
                Coordinate coo = new Coordinate(coordinates);
                co.add(coo);
                currIndex += dims;
            }

            assert currIndex == end;
            */

            c.put(b, subset);
        }
/*
        if(actualNum == expectedNum) {
            logger.info("parsed expected number of coefficients.");
        } else {
            logger.warning(format("expected to parse [%d] coefficients, but parsed [%d] instead", expectedNum, actualNum));
        }
*/
        return unmodifiableMap(c);
    }

    /*
    private int deriveCoordListSize(int numCoefficients, int dims) {
        BigDecimal numerator = new BigDecimal(numCoefficients);
        BigDecimal denominator = new BigDecimal(dims);
        BigDecimal quotient = numerator.divide(denominator, 0, RoundingMode.HALF_UP);
        return quotient.intValue();
    }
    */
}
