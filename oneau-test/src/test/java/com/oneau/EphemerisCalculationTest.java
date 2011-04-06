package com.oneau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.oneau.core.util.Utility.throwIfNull;
import static org.junit.Assert.*;

import com.oneau.core.Ephemeris;
import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import com.oneau.data.DAOFactory;
import com.oneau.data.EphemerisDAO;
import com.oneau.data.EphemerisIntervalNotFound;
import com.oneau.test.EphemerisTestCase;

public class EphemerisCalculationTest {
	private static final String TEST_CASES = "/testpo.405";
	private static final Set<EphemerisTestCase> testCases = new HashSet<EphemerisTestCase>();
	
	private EphemerisDAO ephemerisDao;
	
	@BeforeClass
	public static void loadTestEphemerisData() throws IOException {
		InputStream is = EphemerisCalculationTest.class.getResourceAsStream(TEST_CASES);
		throwIfNull(TEST_CASES, is);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String row;
		int id=0;
		while((row = r.readLine())!=null) {
			id++;
			if(row.trim().equals("EOT")) {
				break;
			}
		}
		
		while((row = r.readLine())!=null) {
			id++;
			EphemerisTestCase t = new EphemerisTestCase(id, row);
			testCases.add(t);
		}
	}
	
	@Before
	public void setUp() {
		this.ephemerisDao = DAOFactory.instance().getEphemerisDAO();
	}
	
	@Test
    public void testApp() throws IOException {
		Ephemeris e = new Ephemeris(ephemerisDao);
    	for(EphemerisTestCase tc : testCases) {
    		System.out.println(tc);
    		HeavenlyBody body = HeavenlyBody.lookup(tc.getTargetBody());
    		if(body.isBody()) {
	    		try {
	    			Map<HeavenlyBody, PositionAndVelocity> result = e.calculatePlanetaryEphemeris(tc.getJulianDate(), body);
		    		PositionAndVelocity pv = result.get(body);
		    		assertNotNull(pv);
		    		assertEquals(tc.getJulianDate(), pv.getEphemerisDate());
		    		Double expected = tc.getCoordinate();
		    		Double actual = pv.getPosition()[tc.getCoordinateId()];
		    		assertEquals(expected, actual);
	    		} catch (EphemerisIntervalNotFound ex) {
	    			System.err.println(ex);
	    		}
    		}
    	}
    }
}
