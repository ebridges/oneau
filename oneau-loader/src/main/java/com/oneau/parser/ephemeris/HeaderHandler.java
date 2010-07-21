package com.oneau.parser.ephemeris;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public interface HeaderHandler {
    void handle(Header header, BufferedReader data) throws IOException;
}
