package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.core.util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
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
    private final Header header;
    private final String filename;
    private final Integer observationNum;
    private final Integer coefficientCount;

    public ObservationParser(Header header, String filename, Integer observationNum, Integer coefficientCount) {
        this.header = header;
        this.filename = filename;
        this.observationNum = observationNum;
        this.coefficientCount = coefficientCount;
    }

    public Observation parseObservation(BufferedReader reader) throws IOException {
        Observation o = new Observation(filename, observationNum);
        
        List<BigDecimal> coefficients = readAllCoefficients(reader);

        if(logger.isLoggable(Level.INFO)){
            logger.info(format("Read %d coefficients for observation %s#%d.", coefficients.size(), filename,observationNum ));
        }

        // first two coefficients are begin/end dates.
        o.setBeginEndDates(new Range<BigDecimal>(
            coefficients.get(0),
            coefficients.get(1)
        ));

        o.setCoefficients(
            gatherPlanetaryCoefficients(header, coefficients)
        );

        return o;
    }

    private List<BigDecimal> readAllCoefficients(BufferedReader reader) throws IOException {
        // add 2 to account for begin & end dates at beginning of list of coefficients
        int numCount = coefficientCount+2;
        int lines = numCount/3;
        int lineCount = 0;
        int coefficientCountCheck = 0;
        
        List<BigDecimal> coefficients = new ArrayList<BigDecimal>(numCount);

        String line = null;

        logger.info(format("Expecting to read %d lines and %d coefficients for this observation.", lines, numCount));
        
        while( lineCount < lines ) {
            line = reader.readLine();

            if(isEmpty(line)) {
                throw new IllegalArgumentException("premature end of observation!");
            }

            String[] fields = line.trim().split("\\s+");
            if(fields.length != 3) {
            	throw new IllegalArgumentException(format("expected 3 coefficients from line [%s]", line));
            }

            for(String f : fields) {
            	BigDecimal d = parseCoefficient(f);
            	logger.finest(format("coefficient #%d parsed from %s to %10.25f", coefficientCountCheck, f, d));
                coefficients.add(d);
                coefficientCountCheck++;
            }

            lineCount++;
        }

        if(coefficientCountCheck != numCount) {
            throw new IllegalStateException(format("observation #%d in file %s had incorrect number of coefficients. Expected %d, but was %d", observationNum, filename, numCount, coefficientCountCheck));
        } else {
        	logger.info(format("observation #%d in file %s had correct number of coefficients. Expected %d, and was %d", observationNum, filename, numCount, coefficientCountCheck));
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
    private Map<HeavenlyBody, List<BigDecimal>> gatherPlanetaryCoefficients(final Header header, final List<BigDecimal> coefficients) {
        Map<HeavenlyBody, List<BigDecimal>> c = new HashMap<HeavenlyBody, List<BigDecimal>>();

        int expectedNum = getExpectedCountOfObservationCoefficients(header.getCoeffInfo());
        int actualNum = 0;
        
        for(HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
            CoefficientInfo ci = header.getCoeffInfo().get(b);
            final int start = ci.getAdjustedIndex();
            final int numCoefficients =  ci.getCoeffSets() * ci.getCoeffCount() * b.getDimensions();
            final int end = ci.getAdjustedIndex() + numCoefficients;
            
            /*
            final int dims = b.getDimensions();
            // there's nothing special about this method, it's just to ensure
            // that rounding up when needed happens
            int numCoordinates = deriveCoordListSize(numCoefficients, dims);
            List<Coordinate> co = new ArrayList<Coordinate>(numCoordinates);
            */

            // this is the subset related to this body
            List<BigDecimal> subset = coefficients.subList(start, end);
            actualNum += subset.size();
            logger.finest(format("    [%s]: %d->%d [%s]", b.getName(), start, end, Utility.toString(subset)));
            
            if(subset.size() != numCoefficients) {
            	throw new IllegalStateException(format("unexpected subset for %s. Expected %d, actual %d.", b.getName(), numCoefficients, subset.size()));
            } else {
            	logger.finer(format("added %d coefficients for body %s", subset.size(), b.getName()));
            }
            
            /*
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

        if(actualNum == expectedNum) {
            logger.finer(format("parsed %d coefficients for all bodies in this observation.", actualNum));
        } else {
            throw new IllegalStateException(format("expected to parse [%d] coefficients, but parsed [%d] instead", expectedNum, actualNum));
        }

        return unmodifiableMap(c);
    }

	private int getExpectedCountOfObservationCoefficients(Map<HeavenlyBody, CoefficientInfo> map) {
		int expectedCoefficientCount = 0;
        for(HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
        	CoefficientInfo ci = map.get(b);
        	expectedCoefficientCount +=  ci.getCoeffSets() * ci.getCoeffCount() * b.getDimensions();
        }
		return expectedCoefficientCount;
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
