package com.oneau.parser.ephemeris;

import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.oneau.common.ObservationWriter;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class AscpFileParser {
    /**
     * Regular expression to match an observation header line in an ascp file.
     * e.g. '     1  1018'
     *      '   229  1018'
     */
    private static final Pattern OBSERVATION_HEADER_PATTERN = Pattern.compile("^\\s{3,5}");

    private static final Logger logger = Logger.getLogger(AscpFileParser.class.getName());
    private Header header;
    private String filename;
    private int expectedObservationCount
    ;

    public AscpFileParser(Header header, String filename) {
        this.header = header;
        this.filename = filename;
    }

    public void readObservationsFromFile(ObservationWriter writer) throws IOException {
    	BufferedReader reader = null;
    	String line = null;
    	int observationCount = 0;
    	try {
        	reader = readFile();
        	
	        while((line = reader.readLine()) != null) {
	            if(isEmpty(line)) {
	                continue;
	            }
	
	        	if(isObservationHeaderLine(line)) {
	                String[] fields = line.trim().split("\\s+");
	                logger.finer(format("line: (%s) [fields[0]=%s, fields[1]=%s]", line, fields[0], fields[1]));
	                Integer observationNum = Integer.parseInt(fields[0]);
	                Integer coefficientCount = Integer.parseInt(fields[1]);
	
	                ObservationParser parser = new ObservationParser(header, filename, observationNum, coefficientCount);
	                Observation observation = parser.parseObservation(reader);
	
	                writer.write(header, observation);
	                observationCount++;
	            }
	        }
	        if(observationCount != expectedObservationCount) {
	        	throw new IllegalStateException(format("read an unexpected number of observations. expected %d, but only read %d", expectedObservationCount, observationCount));
	        } else {
	        	logger.info(format("read %d observations from %s",observationCount,filename));
	        }
		} finally {
			if(null != reader) 
				reader.close();
		}
    }

	private BufferedReader readFile() throws IOException {
		InputStream is = null;
		BufferedReader reader = null;
        is = getClass().getResourceAsStream(filename);
        if(null == is) {
            throw new IllegalStateException(format("unable to locate ascp file [%s] on classpath", filename));
        }
        reader = new BufferedReader(
            new InputStreamReader(
                is
            )
        );
        
        String line = null;
        while((line = reader.readLine()) != null) {
        	if(isObservationHeaderLine(line)) {
        		this.expectedObservationCount++;
        	}
        }
        
        logger.info(format("%s has %d observations.", filename, expectedObservationCount));
        
        reader.close();
        
        is = getClass().getResourceAsStream(filename);
        if(null == is) {
            throw new IllegalStateException(format("unable to locate ascp file [%s] on classpath", filename));
        }
        reader = new BufferedReader(
            new InputStreamReader(
                is
            )
        );
        
        return reader;
    }
	
	private boolean isObservationHeaderLine(String line) {
		return line.startsWith("   ");
    	//return OBSERVATION_HEADER_PATTERN.matcher(line).matches();
	}
}
