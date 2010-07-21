package com.oneau.parser.ephemeris;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class Group1030HandlerTest {
    private static final Double START_EPOCH = 2305424.50;
    private static final Double END_EPOCH = 2525008.50;

    private static final String HEADER1030 = format("GROUP   1030\n \n  %f  %f         32.\n", START_EPOCH, END_EPOCH);

    private HeaderHandlerFactory.Group1030Handler underTest;

    public Group1030HandlerTest() {
        this.underTest = new HeaderHandlerFactory.Group1030Handler();
    }

    @Test
    public void testGroupParsing() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(HEADER1030));
        Header header = new Header("filename");
        this.underTest.handle(header, reader);
        assertEquals(START_EPOCH, header.getStartEpoch());
        assertEquals(END_EPOCH, header.getEndEpoch());
    }
}
