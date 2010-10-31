package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

/**
 * User: ebridges
 * Date: Aug 28, 2010
 */
public class NoOpObservationWriter implements ObservationWriter {
    @Override
    public void write(Header header, Observation observation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
