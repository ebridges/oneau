package com.oneau.core.util;

import static com.oneau.core.util.Utility.*;
import static com.oneau.core.util.Utility.containsAll;
import static com.oneau.core.util.Utility.containsAny;
import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

public class UtilityTest {
	private static final Logger logger = Logger.getLogger(Utility.class);
    private Object[] underTest = {1, 2, 3, 4};

    @Test
    public void testToJulianDay() {
        double expected = 2451544.5;
        double actual = Utility.toJulianDay(2000.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
    
        assertEquals(expected, actual, 0.01);
    }

    @Test
    public void testFirst() {
        assertEquals(1, first(underTest));
    }

    @Test
    public void testRest() {
        Object[] expected = {2, 3, 4};
        Object[] actual = rest(underTest);
        assertEquals(expected.length, actual.length);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testContainsAny_All() {
        boolean actual = containsAny(underTest, 1, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAny_Some() {
        boolean actual = containsAny(underTest, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAny_None() {
        boolean actual = containsAny(underTest, 5, 6, 7, 8);
        assertFalse(actual);
    }

    @Test
    public void testContainsAny_Empty() {
        boolean actual = containsAny(underTest);
        assertFalse(actual);
    }

    @Test
    public void testContainsAll_All() {
        boolean actual = containsAll(underTest, 1, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAll_Some() {
        boolean actual = containsAll(underTest, 2, 3, 4);
        assertTrue(actual);
    }

    @Test
    public void testContainsAll_None() {
        boolean actual = containsAll(underTest, 5, 6, 7, 8);
        assertFalse(actual);
    }

    @Test
    public void testContainsAll_Empty() {
        boolean actual = containsAll(underTest);
        assertFalse(actual);
    }

    @Test
    public void testParseCoefficient() {
    	int count=1;
    	for(String coefficientTuple : COEFFICIENTS) {
    		if(!isEmpty(coefficientTuple)) {
    			Double baseLine = parseValueBaseLine(coefficientTuple);
    			BigDecimal newImpl = parseValueNew(coefficientTuple);
    			
    			String[] coefficients = coefficientTuple.split("\\s+");
    			String coefficient=null;
    			for(int i=0; i<coefficients.length; i++) {
    				if(!isEmpty(coefficients[i])) {
    					coefficient = coefficients[i];
    					break;
    				}
    			}
    			logger.debug(format("[%s] Baseline [%10.20f] New Impl [%10.25f]", formatPositive(coefficient), baseLine, newImpl));
        		if(count++ == 100) {
        			return;
        		}
    		}
    	}
    }
    
    private String formatPositive(String c) {
    	if(c.startsWith("-")) {
    		return c;
    	} else {
    		return "+" + c;
    	}
	}

    private static Double parseValueBaseLine(String coefficientTuple) {
    	double value;
    	
        /*  parse first entry  */
        int mantissa1 = Integer.parseInt(coefficientTuple.substring(4, 13));
        int mantissa2 = Integer.parseInt(coefficientTuple.substring(13, 22));
        int exponent = Integer.parseInt(coefficientTuple.substring(24, 26));
        if (coefficientTuple.substring(23, 24).equals("+"))
            value = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
        else
            value = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
        if (coefficientTuple.substring(1, 2).equals("-"))
            value = -value;
    	return value;
    }

	private static BigDecimal parseValueNew(String coefficientTuple) {
		String[] coefficients = coefficientTuple.split("\\s+");
		for(int i=0; i<coefficients.length; i++) {
			if(!isEmpty(coefficients[i])) {
				String[] value = coefficients[i].split("D");
				logger.debug(value[0]);
				BigDecimal v = (new BigDecimal(value[0]));
				logger.debug("v1: " + v);
				if(value[1].startsWith("+")) {
					value[1] = value[1].replace("+", "");
				}
				
				v = v.movePointRight(Integer.parseInt(value[1]));
				
				logger.debug("v2: "+v);
				
				return v;
			}
		}
		return null;
	}
	
	private static final String ONE_OBSERVATION = 
			" -0.200633300519376407D-06 -0.197699871989219880D-06  0.205650855898029387D+09  \n" +
			" -0.109415771410544845D+07 -0.144654224756247061D+07  0.635400384433414820D+04  \n" +
			"  0.102277804006979852D+04 -0.116846058148926790D+02 -0.549024989620665704D+00  \n" +
			"  0.141741335012572391D-01  0.272177742596026629D-03 -0.144491320644697574D-04  \n" +
			" -0.365665899874440807D-07  0.151803000600356348D+08  0.328311071966251545D+08  \n" +
			" -0.108118795958534771D+06 -0.377928020595511553D+05  0.277463744660916234D+03  \n" +
			"  0.214747064638794427D+02 -0.385885295548587193D+00 -0.112362104375604809D-01  \n" +
			"  0.422818161184449729D-03  0.461440615090461075D-05 -0.415279336795377355D-06  \n" +
			"  0.141715064567612926D+07  0.150886626749044042D+08 -0.104822773256635992D+05  \n" +
			" -0.175062428703760925D+05  0.996100472148497147D+02  0.101657835496558828D+02  \n" +
			" -3.162143993383701196D+00 -1.553638337555255669D-02  2.186668223082336968D-03  \n" +
			" -1.432953676547702635D+06  0.996100472148497147D+02  0.101657835496558828D+02  \n" ;

			
	private static final List<String> COEFFICIENTS = Arrays.asList(
			ONE_OBSERVATION.split("\n")
		);
}
