package com.oneau.core.util;

import org.junit.Test;

import static com.oneau.core.util.Utility.containsAll;
import static com.oneau.core.util.Utility.containsAny;
import static com.oneau.core.util.Utility.first;
import static com.oneau.core.util.Utility.rest;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: EBridges
 * Created: 2010-04-14
 */
public class UtilityTest {
    private Object[] underTest = {1, 2, 3, 4};

    @Test
    public void testFirst() {
        assertEquals(1, first(underTest));
    }

    @Test
    public void testRest() {
        Object[] expected = {2, 3, 4};
        Object[] actual = rest(underTest);
        assertEquals(expected.length, actual.length);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testContainsAny_All() {
        boolean actual = containsAny(underTest, 1, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAny_Some() {
        boolean actual = containsAny(underTest, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAny_None() {
        boolean actual = containsAny(underTest, 5, 6, 7, 8);
        assertFalse(actual);
    }

    @Test
    public void testContainsAny_Empty() {
        boolean actual = containsAny(underTest);
        assertFalse(actual);
    }

    @Test
    public void testContainsAll_All() {
        boolean actual = containsAll(underTest, 1, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAll_Some() {
        boolean actual = containsAll(underTest, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAll_None() {
        boolean actual = containsAll(underTest, 5, 6, 7, 8);
        assertFalse(actual);
    }

    @Test
    public void testContainsAll_Empty() {
        boolean actual = containsAll(underTest);
        assertFalse(actual);
    }
}
