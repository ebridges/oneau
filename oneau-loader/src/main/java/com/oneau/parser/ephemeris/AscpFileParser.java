package com.oneau.parser.ephemeris;

import com.oneau.core.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void readObservationsFromFile(ObservationWriter writer) throws IOException {
        BufferedReader reader = readFile();
        String line = null;
        try {
            while( (line = reader.readLine()) != null) {
                if(isEmpty(line)) {
                    continue;
                }

                if(line.startsWith("    ")) {
                    String[] fields = line.trim().split("\\s+");
                    logger.finer(format("line: (%s) [fields[0]=%s, fields[1]=%s]", line, fields[0], fields[1]));
                    Integer observationNum = Integer.parseInt(fields[0]);
                    Integer coefficientCount = Integer.parseInt(fields[1]);

                    ObservationParser parser = new ObservationParser(header, filename, observationNum, coefficientCount);
                    Observation observation = parser.parseObservation(reader);

                    writer.write(header, observation);
                }
            }
        } finally {
            reader.close();
        }
    }

    private BufferedReader readFile() throws IOException {
        String file = format(Constants.EPHMERIS_FILE_ROOT, filename);
        InputStream is = getClass().getResourceAsStream(file);
        if(null == is) {
            throw new IllegalStateException(format("unable to locate ascp file [%s] on classpath", filename));
        }
        return new BufferedReader(
            new InputStreamReader(
                is
            )
        );
    }

}
