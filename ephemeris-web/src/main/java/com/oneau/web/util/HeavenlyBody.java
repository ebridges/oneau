package com.oneau.web.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.oneau.web.util.Utility.isEmpty;
import static java.util.Collections.unmodifiableSortedSet;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public enum HeavenlyBody {
    MERCURY(1, "Mercury", 4, 14),
    VENUS(2, "Venus", 2, 10),
    EARTH(3, "Earth", 2, 13),
    MARS(4, "Mars", 1, 11),
    JUPITER(5, "Jupiter", 1, 8),
    SATURN(6, "Saturn", 1, 7),
    URANUS(7, "Uranus", 1, 6),
    NEPTUNE(8, "Neptune", 1, 6),
    PLUTO(9, "Pluto", 1, 6),
    MOON(10, "Moon", 8, 13),
    SUN(11, "Sun", 2, 11);

    private static final Map<Integer, HeavenlyBody> BY_ID = new HashMap<Integer, HeavenlyBody>();
    private static final Map<String, HeavenlyBody> BY_NAME = new HashMap<String, HeavenlyBody>();
    private static final Comparator<HeavenlyBody> COMPARE_BY_INDEX = new HeavenlyBodyByIndexComparator();
    private static final TreeSet<HeavenlyBody> ORDERED_BY_INDEX = new TreeSet<HeavenlyBody>(COMPARE_BY_INDEX);

    static {
        for (HeavenlyBody b : HeavenlyBody.values()) {
            BY_ID.put(b.getId(), b);
            BY_NAME.put(b.getName().toUpperCase(), b);
        }
        ORDERED_BY_INDEX.addAll(BY_ID.values());
    }

    private Integer id;
    private String name;
    private Integer numberOfChebyshevCoefficients;
    private Integer numberOfCoefficientSets;

    HeavenlyBody(Integer index, String name, Integer coefficientSets, Integer chebyshevCoefficients) {
        this.id = index;
        this.name = name;
        this.numberOfChebyshevCoefficients = chebyshevCoefficients;
        this.numberOfCoefficientSets = coefficientSets;
    }

    public Integer getId() {
        return id;
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

    public static SortedSet<HeavenlyBody> orderedByIndex() {
        return unmodifiableSortedSet(ORDERED_BY_INDEX);
    }

    public static HeavenlyBody lookup(String name) {
        if (isEmpty(name)) {
            return null;
        }
        Integer id = null;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException ignored) {
        }

        if (null != id && BY_ID.containsKey(id)) {
            return BY_ID.get(id);
        } else if (!isEmpty(name) && BY_NAME.containsKey(name.trim().toUpperCase())) {
            return BY_NAME.get(name.trim().toUpperCase());
        } else {
            return null;
        }
    }
}

class HeavenlyBodyByIndexComparator implements Comparator<HeavenlyBody> {
    public int compare(HeavenlyBody left, HeavenlyBody right) {
        return left.getId() - right.getId();
    }
}