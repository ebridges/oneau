package com.oneau.parser.ephemeris;

import java.io.BufferedReader;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public interface AscpHandler {
    Observation handle(Header header, BufferedReader reader);
}
