package com.oneau.loader.ephemeris;

import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.io.OutputStreamWriter;

/**
 * User: ebridges
 * Date: Nov 13, 2010
 */
public class StdoutSqlObservationWriter  extends SqlObservationWriter {
    public StdoutSqlObservationWriter() {
        this(SqlGeneratorFactory.HSQL);
    }

    public StdoutSqlObservationWriter(String dbtype) {
        super(new OutputStreamWriter(System.out), SqlGeneratorFactory.HSQL);
    }
}
