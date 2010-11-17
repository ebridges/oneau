package com.oneau.common;

import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public interface ObservationWriter {
    void init();
    void write(Header header, Observation observation);
    void finish();
}
