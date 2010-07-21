package com.oneau.loader.ephemeris;

import com.oneau.core.util.Utility;

import static com.oneau.core.util.Utility.instantiate;

/**
 * User: ebridges
 * Date: Jul 19, 2010
 */
public class EphemerisWriterFactory {
    public static EphemerisWriter getWriter(EphemerisWriterType type) {
        return instantiate(type.getWriterClass());
    }
}
