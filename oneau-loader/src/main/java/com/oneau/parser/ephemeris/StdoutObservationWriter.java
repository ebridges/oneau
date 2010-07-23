package com.oneau.parser.ephemeris;

import com.oneau.core.util.Coordinate;
import com.oneau.core.util.HeavenlyBody;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.out;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class StdoutObservationWriter implements ObservationWriter {
    @Override
    public void write(Header header, Observation observation) {
        out.println(format("Header Filename: [%s]",header.getFilename()));
        out.println(format("Name: [%s]",header.getName()));
        out.println(format("KSize: [%d]",header.getKsize()));
        out.println(format("NCoeff: [%d]",header.getNumCoeff()));
        out.println(format("Start: [%f]",header.getStartEpoch()));
        out.println(format("End: [%f]",header.getEndEpoch()));
        out.println(format("Observation Start: [%f]",observation.getBeginEndDates().getLeft()));
        out.println(format("Observation End: [%f]",observation.getBeginEndDates().getRight()));

        if(null == header.getConstantNames()) {
            throw new IllegalArgumentException("null list of constant names");
        }
        for(String c : header.getConstantNames()) {
            out.println(format("Constant: %s=%f",c, header.getConstantValue(c)));
        }
        
        out.println(format("Observation %s/#%d", observation.getFilename(), observation.getObservationNumber()));
        Map<HeavenlyBody, CoefficientInfo> m = header.getCoeffInfo();
        Map<HeavenlyBody, List<Double>> c = observation.getCoefficients();
        if(null == m) {
            throw new IllegalArgumentException("null map of coeffInfo");
        }
        for(HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
            out.println(format("CoeffInfo: %s",m.get(b).toString()));
            out.println(format("Coefficients: %s",c.get(b).toString()));
        }
    }
}
