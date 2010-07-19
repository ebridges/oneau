package com.oneau.core.util;

import org.apache.log4j.Logger;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: EBridges
 * Created: 2010-04-09
 */
public class AssertionUtil {
    private static final Logger logger = Logger.getLogger(AssertionUtil.class);

    public static void assertArraysEqual(Double[] expected, Double[] actual) {
        assertEquals(expected.length, actual.length);
        int sz = expected.length;
        for(int i=0; i<sz; i++) {
            if(null == expected[i]) {
                assertNull(format("expected null at idx[%d]",i), actual[i]);
                continue;
            }
            if(null == actual[i]) {
                assertNull(format("expected null at idx[%d]", i), expected[i]);
                continue;
            }
            if(logger.isTraceEnabled()) {
                logger.trace(format("i=>%d::e:[%f]::a:[%f]",i, expected[i], actual[i]));
            }
            assertEquals(format("idx[%d]",i),expected[i], actual[i], 0.000000001);
        }
    }

    /*
    public static void assertArraysEqual(double[] expected, double[] actual) {
        assertEquals(expected.length, actual.length);
        int sz = expected.length;
        for(int i=0; i<sz; i++) {
            assertEquals(format("idx[%d]",i),expected[i], actual[i], 0.000000001);
        }
    }
    */
}
