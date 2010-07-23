package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import org.apache.log4j.Logger;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * User: ebridges
 * Date: Jul 23, 2010
 */
class EphemerisDataViewImpl implements EphemerisDataView {
    private static final Logger logger = Logger.getLogger(EphemerisDataViewImpl.class);
    private final HeavenlyBody body;
    private final Double asOf;
    private List<Double> ephemerisCoefficients;
    private EphemerisDataFile dataFile;

    public EphemerisDataViewImpl(EphemerisDataFile dataFile, HeavenlyBody body, Double julianEphemerisDate) {
        this.dataFile = dataFile;
        this.body = body;
        this.asOf = julianEphemerisDate;
    }

    protected void setCoefficients(Double[] ephemerisCoefficients) {
        this.ephemerisCoefficients = asList(ephemerisCoefficients);
    }

    @Override
    public List<Double> getCoefficients() {
        return unmodifiableList(ephemerisCoefficients.subList(
                getViewBegin(),
                getViewEnd()
        ));
    }

    @Override
    public HeavenlyBody getBody() {
        return body;
    }

    @Override
    public Double getAsOf() {
        return asOf;
    }

    @Override
    public double getChebyshevTime() {
        return 2 * (asOf - ((dataFile.getSubinterval(body, asOf) - 1) * dataFile.getSubintervalDuration(body) + dataFile.getIntervalStartTime(asOf))) / dataFile.getSubintervalDuration(body) - 1;
    }

    @Override
    public int getViewBegin() {
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

    @Override
    public int getViewEnd() {
        return getViewBegin() + (3 * body.getNumberOfChebyshevCoefficients());
    }
}
