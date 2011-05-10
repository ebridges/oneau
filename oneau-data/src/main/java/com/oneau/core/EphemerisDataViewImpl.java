package com.oneau.core;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

import com.oneau.core.util.ChebyshevTime;
import com.oneau.core.util.HeavenlyBody;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * User: ebridges
 * Date: Sep 1, 2010
 */
public class EphemerisDataViewImpl implements EphemerisDataView {
    private static final Logger logger = Logger.getLogger(EphemerisDataViewImpl.class);
    
    private HeavenlyBody body;
    private List<Double> coefficients;
    private ChebyshevTime interpolatedTime;
    private Double asOf;
    private EphemerisDataFile fileProperties;

    public EphemerisDataViewImpl(EphemerisDataFile file, HeavenlyBody planet, Double asOf, ChebyshevTime interpolatedTime, List<Double> coefficients) {
        this.asOf = asOf;
        this.interpolatedTime = interpolatedTime;
        this.body = planet;
        this.coefficients = coefficients;
        this.fileProperties = file;
    }

    @Override
    public List<Double> getCoefficients() {
        return unmodifiableList(coefficients.subList(
                getViewBegin(),
                getViewEnd()
        ));
    }

    @Override
    public HeavenlyBody getBody() {
        return body;
    }

    @Override
    public Double getChebyshevTime() {
        return this.interpolatedTime.getTimeWithinSubinterval(body.getNumberOfCoefficientSets(), asOf);
    }
    
    
    int getViewBegin() {
        int interval = this.fileProperties.getInterval(asOf);
        int subinterval = this.fileProperties.getSubinterval(body, asOf);
        int numbersToSkip = (interval - 1) * this.fileProperties.getNumbersPerInterval();

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

        return pointer-1;
    }

    int getViewEnd() {
        return getViewBegin() + (3 * body.getNumberOfChebyshevCoefficients());
    }    
}
