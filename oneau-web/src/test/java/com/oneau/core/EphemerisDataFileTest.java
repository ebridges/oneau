package com.oneau.core;

import com.oneau.core.util.HeavenlyBody;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;

/**
 * User: EBridges
 * Created: 2010-04-20
 */
public class EphemerisDataFileTest {
    private static final Logger logger = Logger.getLogger(EphemerisDataFileTest.class);

    //private static final String TEST_DATE_FILE = "/julian-days.dat.gz";
    private static final String TEST_DATE_FILE = "/dates.txt";

    @Test
    public void testGetInterval() throws IOException {
        BufferedReader r = loadData(TEST_DATE_FILE);
        String line;
        try {
            while((line = r.readLine()) != null) {
                String[] vals = line.split(",");
                if(vals.length == 2) {
                    Double asOf = Double.valueOf(vals[1]);
                    EphemerisDataFile file = EphemerisDataFile.lookupByDate(asOf);

                    if(null != file) {
                        logger.info("testing against file: "+ file.getFileName());
                    } else {
                        logger.info("no file found for date: "+ asOf);
                        continue;
                    }

                    // test getInterval
                    int expectedInterval = EphemerisReferenceImplementation.getInterval(asOf, file);
                    int actualInterval = file.getInterval(asOf);
                    assertEquals(expectedInterval, actualInterval);

                    // test getIntervalStartTime
                    Double expectedIntervalStartTime = EphemerisReferenceImplementation.getIntervalStartTime(asOf, file);
                    Double actualIntervalStartTime = file.getIntervalStartTime(asOf);
                    assertEquals(expectedIntervalStartTime, actualIntervalStartTime);

                    // test getSubinterval
                    for(HeavenlyBody body : HeavenlyBody.values()) {
                        int expectedSubinterval = EphemerisReferenceImplementation.getSubinterval(asOf, body, file);
                        int actualSubinterval = file.getSubinterval(body, asOf);
                        assertEquals(expectedSubinterval, actualSubinterval);
                    }
                }
            }
        } finally {
            if(null != r)
                r.close();
        }
    }

    @Test
    public void testGetSubintervalDuration() {
        for(EphemerisDataFile file : EphemerisDataFile.values()) {
            for(HeavenlyBody body : HeavenlyBody.values()) {
                double expectedSubintervalDuration = EphemerisReferenceImplementation.getSubintervalDuration(body, file);
                double actualSubintervalDuration = file.getSubintervalDuration(body);
                assertEquals(expectedSubintervalDuration, actualSubintervalDuration, 0.00001);
            }
        }
    }

    private BufferedReader loadData(String file) throws IOException {
        InputStream is = getClass().getResourceAsStream(file);
        if(file.endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(is)));
        } else {
            return new BufferedReader(new InputStreamReader(is));
        }
    }

}
