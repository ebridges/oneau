package com.oneau.parser.ephemeris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class AscpFileParser {
    private static final Logger logger = Logger.getLogger(AscpFileParser.class.getName());
    private Header header;
    private String filename;

    public AscpFileParser(Header header, String filename) {
        this.header = header;
        this.filename = filename;
    }

    public List<Observation> readObservationsFromFile() throws IOException {
        BufferedReader reader = readFile();
        String line = null;
        List<Observation> observations = new LinkedList<Observation>();
        while( (line = reader.readLine()) != null) {
            if(isEmpty(line)) {
                continue;
            }
            AscpHandler h = AscpHandlerFactory.getHandler(line);
            Observation observation = h.handle(header, reader);
            observations.add(observation);
        }
        return observations;
        
    }

    private BufferedReader readFile() throws IOException {
        InputStream is = getClass().getResourceAsStream(filename);
        if(null == is) {
            throw new IllegalStateException(format("unable to locate header file [%s] on classpath", filename));
        }
        return new BufferedReader(
            new InputStreamReader(
                    is
            )
        );
    }

}
