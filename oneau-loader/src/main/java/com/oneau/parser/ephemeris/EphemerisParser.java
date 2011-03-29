package com.oneau.parser.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.core.EphemerisDataFile;
/*
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
*/
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class EphemerisParser {
    private static final Logger logger = Logger.getLogger(EphemerisParser.class.getName());
    private static final String OBSERVATION_WRITER_ARG = "-w";
    private static final String DEFAULT_OBSERVATION_WRITER_CLASS = "com.oneau.loader.ephemeris.StdoutSqlObservationWriter";


    private Map<String,String> arguments;

    public static void main( String[] args ) throws Exception {

        EphemerisParser parser = new EphemerisParser(args);
        parser.run();
    }

    public EphemerisParser(String[] args) {
        this.arguments = new HashMap<String,String>();
        parseArgs(args);
    }

    public void run() throws Exception {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisParser.class.getName(), "run");
        }
        HeaderParser headerParser = new HeaderParser(HeaderParser.HEADER_405);
        Header header = headerParser.readHeader();

        ObservationWriter ow = initializeObservationWriter(arguments.get(OBSERVATION_WRITER_ARG));
        ow.init();
        try {
            for(EphemerisDataFile file : EphemerisDataFile.values()) {
                AscpFileParser coeffParser = new AscpFileParser(header, file.getFileName());
                coeffParser.readObservationsFromFile(ow);
            }
        } finally {
            ow.finish();
        }
    }

    private ObservationWriter initializeObservationWriter(String arg) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String clazz;
        if(isEmpty(arg)) {
            clazz = DEFAULT_OBSERVATION_WRITER_CLASS;
        } else {
            clazz = arg;
        }
        return ObservationWriter.class.cast(Class.forName(clazz).newInstance());
    }

    private void parseArgs(String[] args) {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisParser.class.getName(), "parseArgs", args);
        }

        for(int i=0; i<args.length; i++) {
            arguments.put(args[i], args[++i]);
        }

        /*
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
        */
    }

}
