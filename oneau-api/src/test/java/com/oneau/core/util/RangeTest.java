package com.oneau.core.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: ebridges
 * Date: 5/22/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RangeTest {

    @Test
    public void testLookupFailure() {
        BigDecimal left = new BigDecimal("2524592.50000000000");
        BigDecimal right = new BigDecimal("2524624.50000000000");
        Range<BigDecimal> undertest = new Range<BigDecimal>(left, right);
        Map<Range<BigDecimal>, Long> lookup = new HashMap<Range<BigDecimal>, Long>();
        lookup.put(undertest, 100L);
        assertTrue(lookup.containsKey(undertest));
    }
}
