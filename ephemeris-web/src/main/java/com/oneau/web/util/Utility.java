package com.oneau.web.util;

/**
 * User: ebridges
 * Date: Feb 19, 2010
 */
public final class Utility {
    public static boolean isEmpty(String s) {
        return null == s || s.length() < 1;
    }

    public static String toCsv(double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for(int i=1; i<=3; i++) {
            if(i>1) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }
    
    private Utility() {
    }
}
