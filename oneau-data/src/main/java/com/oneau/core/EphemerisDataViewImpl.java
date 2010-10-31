package com.oneau.core;

import com.oneau.core.util.ChebyshevTime;
import com.oneau.core.util.HeavenlyBody;

import java.util.List;

/**
 * User: ebridges
 * Date: Sep 1, 2010
 */
public class EphemerisDataViewImpl implements EphemerisDataView {
    private HeavenlyBody body;
    private List<Double> coefficients;
    private ChebyshevTime interpolatedTime;
    private Double asOf;

    public EphemerisDataViewImpl(HeavenlyBody planet, Double asOf, ChebyshevTime interpolatedTime, List<Double> coefficients) {
        this.asOf = asOf;
        this.interpolatedTime = interpolatedTime;
        this.body = planet;
        this.coefficients = coefficients;
    }

    @Override
    public List<Double> getCoefficients() {
        return coefficients;
    }

    @Override
    public HeavenlyBody getBody() {
        return body;
    }

    @Override
    public Double getChebyshevTime() {
        return this.interpolatedTime.getTimeWithinSubinterval(body.getNumberOfCoefficientSets(), asOf);
    }
}
