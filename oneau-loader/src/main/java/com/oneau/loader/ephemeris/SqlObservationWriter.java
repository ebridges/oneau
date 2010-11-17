package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.parser.ephemeris.CoefficientInfo;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Aug 13, 2010
 */
public class SqlObservationWriter extends AbstractObservationWriter {
    private static final Logger logger = Logger.getLogger(SqlObservationWriter.class.getName());
    private SqlGenerator sqlGenerator;
    private Writer out;

    public SqlObservationWriter(Writer w) {
        this.out = w;
        this.sqlGenerator = new SqlGenerator();
    }

    @Override
    public void write(Header header, Observation observation) {
        logger.fine("write() called");
        try {

            String headerInfo = sqlGenerator.writeHeader(header);
            if(null != headerInfo)
                out.write(headerInfo);

            String fileInfo = sqlGenerator.writeFileInfo(observation);
            if(null != fileInfo)
                out.write(fileInfo);

            String rangeInfo = sqlGenerator.writeRangeInfo(observation);
            if(null != rangeInfo)
                out.write(rangeInfo);

            for(String sql : sqlGenerator.writeObservations(observation))
                out.write(sql);
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
