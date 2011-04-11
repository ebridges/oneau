package com.oneau.loader.ephemeris;

import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.io.OutputStreamWriter;

/**
 * User: ebridges
 * Date: Nov 13, 2010
 */
public class StdoutSqlObservationWriter  extends SqlObservationWriter {
    public StdoutSqlObservationWriter(String dbtype) {
        super(new OutputStreamWriter(System.out), dbtype);
    }

    @Override
    public void write(Header header, Observation observation) {
        super.write(header, observation);
    }

    @Override
    public void finish() {
        super.finish();
        System.out.flush();
    }
}
