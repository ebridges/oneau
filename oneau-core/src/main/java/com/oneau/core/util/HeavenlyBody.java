package com.oneau.core.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.oneau.core.util.Utility.isEmpty;
import static java.util.Collections.unmodifiableSortedSet;

class HeavenlyBodyByIndexComparator implements Comparator<HeavenlyBody> {
    public int compare(HeavenlyBody left, HeavenlyBody right) {
        return left.getId() - right.getId();
    }
}