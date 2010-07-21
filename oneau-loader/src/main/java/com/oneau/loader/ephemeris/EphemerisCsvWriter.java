package com.oneau.loader.ephemeris;

import com.oneau.core.EphemerisData;
import com.oneau.core.EphemerisDataFile;
import com.oneau.core.EphemerisDataView;
import com.oneau.core.util.HeavenlyBody;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 19, 2010
 */
public class EphemerisCsvWriter implements EphemerisWriter {
    @Override
    public void print(EphemerisData data) {
        EphemerisDataFile file = data.getDataFile();
        for(HeavenlyBody body : HeavenlyBody.values()) {
            EphemerisDataView dataSet = data.getDataForBody(body, file.getBeginDate());

            String line = format("%s;%s;%f,%f;%s", file.getFileName(), body.getName(), file.getBeginDate(), file.getEndDate(), dataSet.getCoefficients());
            System.out.println(line);
        }
    }
}
