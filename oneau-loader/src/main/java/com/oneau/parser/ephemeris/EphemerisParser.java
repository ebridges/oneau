package com.oneau.parser.ephemeris;

import com.oneau.core.EphemerisDataFile;
import com.oneau.core.util.Constants;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class EphemerisParser {
    private static final Logger logger = Logger.getLogger(EphemerisParser.class.getName());

    public static void main( String[] args ) throws Exception {
        EphemerisParser parser = new EphemerisParser(args);
        parser.run();
    }

    public EphemerisParser(String[] args) {
        parseArgs(args);
    }

    public void run() throws IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisParser.class.getName(), "run");
        }
        HeaderParser headerParser = new HeaderParser(HeaderParser.HEADER_405);
        Header header = headerParser.readHeader();

        for(EphemerisDataFile file : EphemerisDataFile.values()) {
            AscpFileParser coeffParser = new AscpFileParser(header, file.getFileName());
            coeffParser.readObservationsFromFile(new StdoutObservationWriter());
        }
    }

    private void parseArgs(String[] args) {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisParser.class.getName(), "parseArgs", args);
        }
        Options o = new Options();
        Option observationWriter = OptionBuilder
                .hasArg()
                .withLongOpt("observation-writer")
                .withDescription("control where parsed ephemeris observations are written")
                .withArgName("FQCN")
                .create();
        o.addOption(observationWriter);
                
        Option ephemerisFiles = OptionBuilder
                .hasArgs()
                .withLongOpt("ephemeris-files")
                .withDescription(format("which files to parse, if blank does all. searched on classpath under root %s", Constants.EPHMERIS_FILE_ROOT))
                .withArgName("FILENAME(s)")
                .create();
        o.addOption(ephemerisFiles);
    }

}
