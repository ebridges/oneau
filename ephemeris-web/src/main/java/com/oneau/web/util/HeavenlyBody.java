package com.oneau.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public enum HeavenlyBody {
    MERCURY(0,"Mercury", 4, 14),
    VENUS(1,"Venus", 2, 10),
    EARTH(2,"Earth", 2, 13),
    MARS(3,"Mars", 1, 11),
    JUPITER(4,"Jupiter", 1, 8),
    SATURN(5,"Saturn", 1, 7),
    URANUS(6,"Uranus", 1, 6),
    NEPTUNE(7,"Neptune", 1, 6),
    PLUTO(8,"Pluto", 1, 6),
    MOON(9,"Moon", 8, 13),
    SUN(10,"Sun", 2, 11);

    private static final Map<Integer, HeavenlyBody> BY_INDEX = new HashMap<Integer, HeavenlyBody>();

    static {
        for(HeavenlyBody b : HeavenlyBody.values()) {
            BY_INDEX.put(b.getIndex(), b);
        }
    }

    public static HeavenlyBody valueOf(Integer index) {
        if(!BY_INDEX.containsKey(index)) {
            throw new IllegalArgumentException("no HeavenlyBody under index "+ index);
        }
        return BY_INDEX.get(index);
    }

    private Integer index;
    private String name;
    private Integer numberOfChebyshevCoefficients;
    private Integer numberOfCoefficientSets;

    HeavenlyBody(Integer index, String name, Integer coefficientSets, Integer chebyshevCoefficients) {
        this.index = index;
        this.name = name;
        this.numberOfChebyshevCoefficients = chebyshevCoefficients;
        this.numberOfCoefficientSets = coefficientSets;
    }

    public Integer getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Integer getNumberOfChebyshevCoefficients() {
        return numberOfChebyshevCoefficients;
    }

    public Integer getNumberOfCoefficientSets() {
        return numberOfCoefficientSets;
    }

    public static HeavenlyBody lookup(int index) {
        if(BY_INDEX.containsKey(index)) {
            return BY_INDEX.get(index);
        }
        return null;
    }
}
