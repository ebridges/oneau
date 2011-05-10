package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.oneau.core.util.Utility.throwIfNull;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class Observation {
    private String filename;
    private Integer observationNumber;
    private Range<BigDecimal> beginEndDates;
    private Map<HeavenlyBody, List<BigDecimal>> coefficients;

    public Observation(String filename, Integer observationNumber) {
        this.filename = throwIfNull("filename", filename);
        this.observationNumber = throwIfNull("observationNumber", observationNumber);
    }

    public String getFilename() {
        return filename;
    }

    public Integer getObservationNumber() {
        return observationNumber;
    }

    public Range<BigDecimal> getBeginEndDates() {
        return beginEndDates;
    }

    public void setBeginEndDates(Range<BigDecimal> beginEndDates) {
    	BigDecimal diff = beginEndDates.getRight().subtract(beginEndDates.getLeft());
    	if(diff.intValueExact() != 32) {
    		throw new IllegalArgumentException("An observation must be 32 days long.");
    	}
        this.beginEndDates = beginEndDates;
    }

    public Map<HeavenlyBody, List<BigDecimal>> getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Map<HeavenlyBody, List<BigDecimal>> coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Observation that = (Observation) o;

        if (!filename.equals(that.filename)) return false;
        if (!observationNumber.equals(that.observationNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31 * result + observationNumber.hashCode();
        return result;
    }
}
