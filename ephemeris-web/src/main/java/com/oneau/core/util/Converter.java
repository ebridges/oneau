package com.oneau.core.util;

/**
 * User: EBridges
 * Created: 2010-04-19
 */
public interface Converter {
    static enum TYPE {
        KM,
        AU
    }

    Double convert(Double value);
}
