package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

/**
 * User: ebridges
 * Date: Aug 28, 2010
 */
public class NoOpObservationWriter extends AbstractObservationWriter {
    @Override
    public void write(Header header, Observation observation) {
        
    }
}
