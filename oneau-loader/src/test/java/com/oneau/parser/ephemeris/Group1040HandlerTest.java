package com.oneau.parser.ephemeris;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class Group1040HandlerTest {
    private static final int CONSTANT_COUNT = 156;
    private static final String[] CONSTANT_NAMES = new String[] {
"DENUM", "LENUM", "TDATEF", "TDATEB", "CENTER", "CLIGHT", "AU", "EMRAT", "GM1", "GM2", 
"GMB", "GM4", "GM5", "GM6", "GM7", "GM8", "GM9", "GMS", "RAD1", "RAD2", 
"RAD4", "JDEPOC", "X1", "Y1", "Z1", "XD1", "YD1", "ZD1", "X2", "Y2", 
"Z2", "XD2", "YD2", "ZD2", "XB", "YB", "ZB", "XDB", "YDB", "ZDB", 
"X4", "Y4", "Z4", "XD4", "YD4", "ZD4", "X5", "Y5", "Z5", "XD5", 
"YD5", "ZD5", "X6", "Y6", "Z6", "XD6", "YD6", "ZD6", "X7", "Y7", 
"Z7", "XD7", "YD7", "ZD7", "X8", "Y8", "Z8", "XD8", "YD8", "ZD8", 
"X9", "Y9", "Z9", "XD9", "YD9", "ZD9", "XM", "YM", "ZM", "XDM", 
"YDM", "ZDM", "XS", "YS", "ZS", "XDS", "YDS", "ZDS", "BETA", "GAMMA", 
"J2SUN", "GDOT", "MA0001", "MA0002", "MA0004", "MAD1", "MAD2", "MAD3", "RE", "ASUN", 
"PHI", "THT", "PSI", "OMEGAX", "OMEGAY", "OMEGAZ", "AM", "J2M", "J3M", "J4M", 
"C22M", "C31M", "C32M", "C33M", "S31M", "S32M", "S33M", "C41M", "C42M", "C43M", 
"C44M", "S41M", "S42M", "S43M", "S44M", "LBET", "LGAM", "K2M", "TAUM", "AE", 
"J2E", "J3E", "J4E", "K2E0", "K2E1", "K2E2", "TAUE0", "TAUE1", "TAUE2", "DROTEX", 
"DROTEY", "GMAST1", "GMAST2", "GMAST3", "KVC", "IFAC", "PHIC", "THTC", "PSIC", "OMGCX", 
"OMGCY", "OMGCZ", "PSIDOT", "MGMIS", "ROTEX", "ROTEY"
};
    private static final String HEADER1040 = format("GROUP   1040\n" +
            " \n" +
            "   %d\n" +
            "  %s   %s   %s  %s  %s  %s  %s      %s   %s     %s   \n" +
            "  %s     %s     %s     %s     %s     %s     %s     %s     %s    %s  \n" +
            "  %s    %s  %s      %s      %s      %s     %s     %s     %s      %s    \n" +
            "  %s      %s     %s     %s     %s      %s      %s      %s     %s     %s   \n" +
            "  %s      %s      %s      %s     %s     %s     %s      %s      %s      %s   \n" +
            "  %s     %s     %s      %s      %s      %s     %s     %s     %s      %s    \n" +
            "  %s      %s     %s     %s     %s      %s      %s      %s     %s     %s   \n" +
            "  %s      %s      %s      %s     %s     %s     %s      %s      %s      %s   \n" +
            "  %s     %s     %s      %s      %s      %s     %s     %s     %s    %s \n" +
            "  %s   %s    %s  %s  %s  %s    %s    %s    %s      %s  \n" +
            "  %s     %s     %s     %s  %s  %s  %s      %s     %s     %s   \n" +
            "  %s    %s    %s    %s    %s    %s    %s    %s    %s    %s  \n" +
            "  %s    %s    %s    %s    %s    %s    %s    %s     %s    %s    \n" +
            "  %s     %s     %s     %s    %s    %s    %s   %s   %s   %s\n" +
            "  %s  %s  %s  %s  %s     %s    %s    %s    %s    %s \n" +
            "  %s   %s   %s  %s   %s   %s                                 ", CONSTANT_COUNT, "DENUM", "LENUM", "TDATEF", "TDATEB", "CENTER", "CLIGHT", "AU", "EMRAT", "GM1", "GM2",
"GMB", "GM4", "GM5", "GM6", "GM7", "GM8", "GM9", "GMS", "RAD1", "RAD2",
"RAD4", "JDEPOC", "X1", "Y1", "Z1", "XD1", "YD1", "ZD1", "X2", "Y2",
"Z2", "XD2", "YD2", "ZD2", "XB", "YB", "ZB", "XDB", "YDB", "ZDB",
"X4", "Y4", "Z4", "XD4", "YD4", "ZD4", "X5", "Y5", "Z5", "XD5",
"YD5", "ZD5", "X6", "Y6", "Z6", "XD6", "YD6", "ZD6", "X7", "Y7",
"Z7", "XD7", "YD7", "ZD7", "X8", "Y8", "Z8", "XD8", "YD8", "ZD8",
"X9", "Y9", "Z9", "XD9", "YD9", "ZD9", "XM", "YM", "ZM", "XDM",
"YDM", "ZDM", "XS", "YS", "ZS", "XDS", "YDS", "ZDS", "BETA", "GAMMA",
"J2SUN", "GDOT", "MA0001", "MA0002", "MA0004", "MAD1", "MAD2", "MAD3", "RE", "ASUN",
"PHI", "THT", "PSI", "OMEGAX", "OMEGAY", "OMEGAZ", "AM", "J2M", "J3M", "J4M",
"C22M", "C31M", "C32M", "C33M", "S31M", "S32M", "S33M", "C41M", "C42M", "C43M",
"C44M", "S41M", "S42M", "S43M", "S44M", "LBET", "LGAM", "K2M", "TAUM", "AE",
"J2E", "J3E", "J4E", "K2E0", "K2E1", "K2E2", "TAUE0", "TAUE1", "TAUE2", "DROTEX",
"DROTEY", "GMAST1", "GMAST2", "GMAST3", "KVC", "IFAC", "PHIC", "THTC", "PSIC", "OMGCX",
"OMGCY", "OMGCZ", "PSIDOT", "MGMIS", "ROTEX", "ROTEY");


    private HeaderHandlerFactory.Group1040Handler underTest;

    public Group1040HandlerTest() {
        this.underTest = new HeaderHandlerFactory.Group1040Handler();
    }

    @Test
    public void testGroupParsing() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(HEADER1040));
        Header header = new Header("filename");
        this.underTest.handle(header, reader);
        List<String> constants = header.getConstantNames();
        assertEquals(CONSTANT_COUNT, constants.size());
        for(int i=0; i<CONSTANT_COUNT; i++) {
            assertEquals(CONSTANT_NAMES[i], constants.get(i));
        }
    }
}
