package com.oneau.parser.ephemeris;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public interface ObservationWriter {
    void write(Header header, Observation observation);
}
