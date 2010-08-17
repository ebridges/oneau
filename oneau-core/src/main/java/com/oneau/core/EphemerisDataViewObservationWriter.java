package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * User: ebridges
 * Date: Jul 23, 2010
 */
public class EphemerisDataViewObservationWriter extends EphemerisDataViewImpl {// implements ObservationWriter {
    private static final Logger logger = Logger.getLogger(EphemerisDataViewObservationWriter.class);

    private List<Double> coefficients;

    public EphemerisDataViewObservationWriter(EphemerisDataFile dataFile, HeavenlyBody body, Double julianEphemerisDate) {
        super(dataFile, body, julianEphemerisDate);
    }

    @Override
    public List<Double> getCoefficients() {
        if(null == this.coefficients || this.coefficients.isEmpty()) {
            throw new IllegalArgumentException("no coefficients found for body "+super.getBody().getName()+" and date "+super.getAsOf());
        }
        return this.coefficients;
    }

//    @Override
    /*
    public void write(Header header, Observation observation) {
        if(observation.getBeginEndDates().contains(super.getAsOf())) {
            Map<HeavenlyBody, List<Double>> coeffs = observation.getCoefficients();
            this.coefficients = coeffs.get(super.getBody());
        } else {
            logger.info("skipping observation: "+observation);
        }
    }
    */
}
