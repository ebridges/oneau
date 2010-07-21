package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class HeaderParser {
    private static final Logger logger = Logger.getLogger(HeaderParser.class.getName());
    public static final String HEADER_405 = "/ephemeris/header.405";
    private String filename;

    public HeaderParser(String filename) {
        this.filename = filename;
    }

    public Header readHeader() throws IOException {
        BufferedReader reader = readFile();
        Header header = new Header(filename);
        String line = null;
        while( (line = reader.readLine()) != null) {
            if(isEmpty(line)) {
                continue;
            }
            HeaderHandler h = HeaderHandlerFactory.getHandler(line);
            h.handle(header, reader);
        }
        return header;
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

    public static void main(String[] args) throws IOException {
        HeaderParser parser = new HeaderParser(HEADER_405);
        Header h = parser.readHeader();
        logger.info(format("Filename: [%s]",h.getFilename()));
        logger.info(format("Name: [%s]",h.getName()));
        logger.info(format("Start: [%f]",h.getStartEpoch()));
        logger.info(format("End: [%f]",h.getEndEpoch()));
        logger.info(format("KSize: [%d]",h.getKsize()));
        logger.info(format("NCoeff: [%d]",h.getNumCoeff()));
        if(null == h.getConstantNames()) {
            throw new IllegalArgumentException("null list of constant names");
        }
        for(String c : h.getConstantNames()) {
            logger.info(format("Constant: %s=%f",c, h.getConstantValue(c)));
        }
        Map<HeavenlyBody, CoefficientInfo> m = h.getCoeffInfo();
        if(null == m) {
            throw new IllegalArgumentException("null map of coeffInfo");
        }
        for(HeavenlyBody b : HeavenlyBody.orderedByIndex()) {
            logger.info(format("CoeffInfo: %s",m.get(b).toString()));
        }
    }
}
