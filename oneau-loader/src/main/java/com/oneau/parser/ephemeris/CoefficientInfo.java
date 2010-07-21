package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class CoefficientInfo {
    private HeavenlyBody body;
    private Integer startIndex;
    private Integer coeffCount;
    private Integer coeffSets;

    public CoefficientInfo(HeavenlyBody body) {
        this.body = body;
    }

    public HeavenlyBody getBody() {
        return body;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getCoeffCount() {
        return coeffCount;
    }

    public void setCoeffCount(Integer coeffCount) {
        this.coeffCount = coeffCount;
    }

    public Integer getCoeffSets() {
        return coeffSets;
    }

    public void setCoeffSets(Integer coeffSets) {
        this.coeffSets = coeffSets;
    }

    @Override
    public String toString() {
        return format("[%s:%d,%d,%d]",this.getBody().getName(),this.getStartIndex(),this.getCoeffCount(),this.getCoeffSets());
    }
}
