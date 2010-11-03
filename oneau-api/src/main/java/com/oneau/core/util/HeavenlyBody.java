package com.oneau.core.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableSortedSet;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public enum HeavenlyBody {
    MERCURY(1, "Mercury", 4, 14, 3, true),
    VENUS(2, "Venus", 2, 10, 3, true),
    EARTH(3, "Earth", 2, 13, 3, true),
    MARS(4, "Mars", 1, 11, 3, true),
    JUPITER(5, "Jupiter", 1, 8, 3, true),
    SATURN(6, "Saturn", 1, 7, 3, true),
    URANUS(7, "Uranus", 1, 6, 3, true),
    NEPTUNE(8, "Neptune", 1, 6, 3, true),
    PLUTO(9, "Pluto", 1, 6, 3, true),
    MOON(10, "Moon", 8, 13, 3, true),
    SUN(11, "Sun", 2, 11, 3, true),
    NUTATIONS(12, "Nutations", 4, 10, 2, false),
    LIBRATIONS(13, "Librations", 4, 10, 3, false);

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
    private Integer dimensions;
    private Integer numberOfChebyshevCoefficients;
    private Integer numberOfCoefficientSets;
    private boolean isBody;

    HeavenlyBody(Integer index, String name, Integer coefficientSets, Integer chebyshevCoefficients, Integer dimensions, boolean isPlanet) {
        this.id = index;
        this.name = name;
        this.numberOfChebyshevCoefficients = chebyshevCoefficients;
        this.numberOfCoefficientSets = coefficientSets;
        this.dimensions = dimensions;
        this.isBody = isPlanet;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public Integer getNumberOfChebyshevCoefficients() {
        return numberOfChebyshevCoefficients;
    }

    public Integer getNumberOfCoefficientSets() {
        return numberOfCoefficientSets;
    }

    public boolean isBody() {
        return isBody;
    }

    public static SortedSet<HeavenlyBody> orderedByIndex() {
        return unmodifiableSortedSet(ORDERED_BY_INDEX);
    }

    public static HeavenlyBody lookup(Integer id) {
        if (null != id && BY_ID.containsKey(id)) {
            return BY_ID.get(id);
        } else {
            return null;
        }
    }

    public static HeavenlyBody lookup(String name) {
        if (Utility.isEmpty(name)) {
            return null;
        }
        Integer id = null;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException ignored) {
        }

        if (null != id && BY_ID.containsKey(id)) {
            return BY_ID.get(id);
        } else if (!Utility.isEmpty(name) && BY_NAME.containsKey(name.trim().toUpperCase())) {
            return BY_NAME.get(name.trim().toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HeavenlyBody");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", dimensions=").append(dimensions);
        sb.append(", numberOfChebyshevCoefficients=").append(numberOfChebyshevCoefficients);
        sb.append(", numberOfCoefficientSets=").append(numberOfCoefficientSets);
        sb.append('}');
        return sb.toString();
    }
}

class HeavenlyBodyByIndexComparator implements Comparator<HeavenlyBody> {
    public int compare(HeavenlyBody left, HeavenlyBody right) {
        return left.getId() - right.getId();
    }
}