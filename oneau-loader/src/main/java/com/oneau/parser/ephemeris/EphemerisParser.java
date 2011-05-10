package com.oneau.parser.ephemeris;

import com.oneau.common.ObservationWriter;
/*
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class EphemerisParser {
    private static final Logger logger = Logger.getLogger(EphemerisParser.class.getName());
    private static final String OBSERVATION_WRITER_OPTION = "observation-writer";
    private static final String EPHEMERIS_FILE_LIST_OPTION = "ephemeris-files";
    private static final String DEFAULT_OBSERVATION_WRITER_CLASS = "com.oneau.loader.ephemeris.StdoutObservationWriter";

    private Map<String,String> arguments;

    public static void main( String[] args ) throws Exception {
        EphemerisParser parser = new EphemerisParser(args);
        parser.run();
    }

    public EphemerisParser(String[] args) {
        this.arguments = new HashMap<String,String>();
        parseArgs(args);
        
        logger.info("EphemerisParser arguments:");
        for(Map.Entry<String,String> e : arguments.entrySet()) {
        	logger.info(format("  %s: %s", e.getKey(), e.getValue()));
        }
    }

    public void run() throws Exception {
        String[] ephemerisFiles = arguments.get(EPHEMERIS_FILE_LIST_OPTION).split(",");
        String headerFile = removeHeader(ephemerisFiles);
        
        HeaderParser headerParser = new HeaderParser(headerFile);
        Header header = headerParser.readHeader();

        ObservationWriter ow = initializeObservationWriter(arguments.get(OBSERVATION_WRITER_OPTION));
        ow.init();
        
        try {
            for(String file : ephemerisFiles) {
            	if(!isEmpty(file)) {
	                AscpFileParser coeffParser = new AscpFileParser(header, file);
	                coeffParser.readObservationsFromFile(ow);
            	}
            }
        } finally {
            ow.finish();
        }
    }

    private String removeHeader(String[] arr) {
    	for(int i=0; i<arr.length; i++) {
    		if(arr[i].contains("header")) {
    			String header = arr[i];
    			arr[i] = null;
    			return header;
    		}
    	}
    	throw new IllegalArgumentException("ephemeris header file not found in file list.");
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

    private void parseArgs(String[] a) {
        List<String> args = copy(a);
        
        for(int i=0; i<args.size(); i++) {
        	if(args.get(i).startsWith("--")) {
                arguments.put(args.get(i).replaceAll("--", ""), args.get(++i));
        	}
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

	private List<String> copy(String[] aaa) {
		List<String> bbb = new ArrayList<String>(aaa.length);
		for(String a : aaa) {
			if(!isEmpty(a)) {
				bbb.add(a.trim());
			}
		}
		return Collections.unmodifiableList(bbb);
	}
}
