package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;

import java.util.List;

/**
 * User: ebridges
 * Date: Jul 19, 2010
 */
public interface EphemerisDataView {
    List<Double> getCoefficients();

    HeavenlyBody getBody();

    Double getAsOf();

    double getChebyshevTime();

    int getViewBegin();

    int getViewEnd();
}
