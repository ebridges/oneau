package com.oneau.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;

public class TestParseEphemeris {
	private static int origCount=0;
	private static int newCount=0;

	public static void main(String args[]) throws Exception {
		String file = args[0];
		BufferedReader r = new BufferedReader(new FileReader(file));
		String l = null;
		while((l = r.readLine()) != null) {
				parseValueOrig(l);
				parseValueNew(l);
		}
	}
	
	private static void parseValueNew(String line) {
		String[] values = splitLine(line);
		for(String val : values) {
			++newCount;
			if(newCount >= 1 && newCount <= 12) {
				String[] value = val.split("D");
		//		System.out.println("value: "+val);
		//		System.out.println("value[0]="+value[0]+" value[1]="+value[1]);
				BigDecimal v = new BigDecimal(value[0]);
				if(value[1].startsWith("+")) {
					String x = value[1].replace("+", "");
					v = v.movePointRight(Integer.parseInt(x));
				} else {
					v = v.movePointLeft(Integer.parseInt(value[1]));
				}
				System.out.println("N:"+(newCount)+": "+v);
			}
		}
	}
	
	private static void parseValueOrig(String line) {
		++origCount;
		if(origCount >= 1 && origCount <= 12) {
			double valueA, valueB, valueC;
	        /*  parse first entry  */
			/* [0.230542450000000000D+07  ] */
			/* [  0.245153650000000000D+07] */
			/* [01234567890123456789012345] */
			/* [12345678901234567890123456] */

	        int mantissa1 = Integer.parseInt(line.substring(4, 13));
	        int mantissa2 = Integer.parseInt(line.substring(13, 22));
	        int exponent = Integer.parseInt(line.substring(24, 26));
	        if (line.substring(23, 24).equals("+"))
	        	valueA = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
	        else
	        	valueA = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
	        if (line.substring(1, 2).equals("-"))
	        	valueA = -valueA;
	
	        /*  parse second entry  */
	        mantissa1 = Integer.parseInt(line.substring(30, 39));
	        mantissa2 = Integer.parseInt(line.substring(39, 48));
	        exponent = Integer.parseInt(line.substring(50, 52));
	        if (line.substring(49, 50).equals("+"))
	        	valueB = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
	        else
	        	valueB = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
	        if (line.substring(27, 28).equals("-"))
	        	valueB = -valueB;
	        
            /*  parse third entry  */
	        mantissa1 = Integer.parseInt(line.substring(56, 65));
	        mantissa2 = Integer.parseInt(line.substring(65, 74));
	        exponent = Integer.parseInt(line.substring(76, 78));
	        if (line.substring(75, 76).equals("+"))
	        	valueC = mantissa1 * Math.pow(10, (exponent - 9)) + mantissa2 * Math.pow(10, (exponent - 18));
	        else
	        	valueC = mantissa1 * Math.pow(10, -(exponent + 9)) + mantissa2 * Math.pow(10, -(exponent + 18));
	        if (line.substring(53, 54).equals("-"))
	        	valueC = -valueC;
	        
	        System.out.println("O:"+(origCount)+": "+valueA);
	        System.out.println("O:"+(++origCount)+": "+valueB);
	        System.out.println("O:"+(++origCount)+": "+valueC);
		}
	}

	private static String[] splitLine(String l) {
		return l.trim().split("\\s+");
	}
	
}
