package com.oneau.loader.ephemeris;

import com.oneau.core.EphemerisData;
import com.oneau.core.EphemerisDataFile;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class EphemerisLoader {
    private static final Logger logger = Logger.getLogger(EphemerisLoader.class.getName());

    public EphemerisLoader(String[] args) {
        parseArgs(args);
    }

    private void parseArgs(String[] args) {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisLoader.class.getName(), "parseArgs", args);
        }
    }

    public void run() throws IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(EphemerisLoader.class.getName(), "run");
        }
        EphemerisWriter writer = EphemerisWriterFactory.getWriter(EphemerisWriterType.CSV_WRITER);
        Set<EphemerisDataFile> files = EphemerisDataFile.values();

        for(EphemerisDataFile file : files) {
            logger.fine(format("parsing ephemeris filename [%s]", file.getFileName()));
            EphemerisData data = new EphemerisData(file);
            writer.print(data);
        }

    }

    public static void main( String[] args ) {
        EphemerisLoader loader = new EphemerisLoader(args);
        try {
            loader.run();
        } catch (IOException e) {
            logger.throwing(EphemerisLoader.class.getName(), "main", e);
        }
    }
}
