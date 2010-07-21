package com.oneau.parser.ephemeris;

import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class FirstLineHandlerTest {
    private HeaderHandlerFactory.FirstLineHandler underTest;
    private static final Integer KSIZE = 2036;
    private static final Integer NCOEFF = 1018;
    private static final String FIRST_LINE = format("KSIZE=  %d    NCOEFF=  %d", KSIZE, NCOEFF);

    public FirstLineHandlerTest() {
        this.underTest = new HeaderHandlerFactory.FirstLineHandler(FIRST_LINE);
    }

    @Test
    public void testFirstLineParsing() {
        Header header = new Header("filename");
        try {
            this.underTest.handle(header, null);
        } catch (java.io.IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals(KSIZE, header.getKsize());
        assertEquals(NCOEFF, header.getNumCoeff());
    }
}
