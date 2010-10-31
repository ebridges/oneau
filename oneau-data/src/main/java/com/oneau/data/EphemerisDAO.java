package com.oneau.data;

import com.oneau.core.EphemerisDataFile;
import com.oneau.core.EphemerisDataView;
import com.oneau.core.util.HeavenlyBody;

/**
 * Date: Aug 17, 2010
 * Time: 3:16:33 PM
 */
public interface EphemerisDAO {
    EphemerisDataView getCoefficientsByDate(EphemerisDataFile source, HeavenlyBody planet, Double julianDate);
}
