package com.oneau.loader.ephemeris;

import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: ebridges
 * Date: Aug 13, 2010
 */
public class SqlObservationWriter extends AbstractObservationWriter {
    private static final Logger logger = Logger.getLogger(SqlObservationWriter.class.getName());
    private SqlGenerator sqlGenerator;
    private Writer out;

    public SqlObservationWriter(Writer w, String dbtype) {
        this.out = w;

        logger.info("Using database type: "+dbtype);
        this.sqlGenerator = SqlGeneratorFactory.instance(dbtype);
    }

    @Override
    public void init() {
        super.init();
        List<String> schema = this.sqlGenerator.generateSchema();
        try {
            for(String s : schema) {
                    out.write(s);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void finish() {
        super.finish();
        System.out.flush();
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
