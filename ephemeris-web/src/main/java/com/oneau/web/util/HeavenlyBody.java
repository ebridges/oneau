package com.oneau.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public enum HeavenlyBody {
    MERCURY(1,"Mercury"),
    VENUS(2,"Venus"),
    EARTH(3,"Earth/Moon (Barycentric)"),
    MARS(4,"Mars"),
    JUPITER(5,"Jupiter"),
    SATURN(6,"Saturn"),
    URANUS(7,"Uranus"),
    NEPTUNE(8,"Neptune"),
    PLUTO(9,"Pluto"),
    MOON(10,"Moon (geocentric)"),
    SUN(11,"Sun");

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

    HeavenlyBody(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
