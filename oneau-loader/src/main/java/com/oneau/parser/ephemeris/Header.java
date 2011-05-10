package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class Header {
    private String filename;
    private Integer ksize;
    private Integer numCoeff;
    private String name;
    private Double startEpoch;
    private Double endEpoch;
    private List<String> constantNames;
    private List<BigDecimal> constantValues;
    private Map<HeavenlyBody, CoefficientInfo> coeffInfo;
    private Integer daysInInterval;


    public Header(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public Integer getKsize() {
        return ksize;
    }

    public void setKsize(Integer ksize) {
        this.ksize = ksize;
    }

    public Integer getNumCoeff() {
        return numCoeff;
    }

    public void setNumCoeff(Integer numCoeff) {
        this.numCoeff = numCoeff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Double startEpoch) {
        this.startEpoch = startEpoch;
    }

    public Double getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(Double endEpoch) {
        this.endEpoch = endEpoch;
    }

    public List<String> getConstantNames() {
        return constantNames;
    }

    public void setConstantNames(List<String> constantNames) {
        if(null == constantNames){
            throw new IllegalArgumentException("null constant names list");
        }
        this.constantNames = constantNames;
    }

    public List<BigDecimal> getConstantValues() {
        return constantValues;
    }

    public void setConstantValues(List<BigDecimal> constantValues) {
        this.constantValues = constantValues;
    }

    public BigDecimal getConstantValue(String name) {
        for(int i=0; i<constantNames.size(); i++) {
            if(constantNames.get(i).equals(name)) {
                return constantValues.get(i);
            }
        }
        return null;
    }

    public Map<HeavenlyBody, CoefficientInfo> getCoeffInfo() {
        return coeffInfo;
    }

    public void setCoeffInfo(Map<HeavenlyBody, CoefficientInfo> coeffInfo) {
        this.coeffInfo = coeffInfo;
    }

    public void setDaysInInterval(int daysInInterval) {
        this.daysInInterval = daysInInterval;
    }

    public Integer getDaysInInterval() {
        return daysInInterval;
    }
}
