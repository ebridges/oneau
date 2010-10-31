package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class CoefficientInfo {
    private HeavenlyBody body;
    private Integer fileStartIndex;
    private Integer coeffCount;
    private Integer coeffSets;

    public CoefficientInfo(HeavenlyBody body) {
        this.body = body;
    }

    public HeavenlyBody getBody() {
        return body;
    }

    public Integer getAdjustedIndex() {
        return fileStartIndex  // provides the begin position for a set of coefficients in the file
                - 1  // adjusts this since the index in the file is "1" based: we need it to be "0" based
              //  - 2 // further adjusts this index since we don't include the 2 date fields that begin every observation
        ;
    }

    public Integer getFileStartIndex() {
        return fileStartIndex;
    }

    public void setFileStartIndex(Integer fileStartIndex) {
        this.fileStartIndex = fileStartIndex;
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
        return format("[%s:%d,%d,%d]",this.getBody().getName(),this.getFileStartIndex(),this.getCoeffCount(),this.getCoeffSets());
    }
}
