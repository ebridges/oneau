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
public class Group1010HandlerTest {
    private static final String NAME = "JPL Planetary Ephemeris DE405/DE405";
    private static final String HEADER1010 = format("GROUP   1010\n\n%s\nStart Epoch: JED=  2305424.5 1599 DEC 09 00:00:00\nFinal Epoch: JED=  2525008.5 2201 FEB 20 00:00:00\n", NAME);

    private HeaderHandlerFactory.Group1010Handler underTest;

    public Group1010HandlerTest() {
        this.underTest = new HeaderHandlerFactory.Group1010Handler();
    }

    @Test
    public void testGroupParsing() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(HEADER1010));
        Header header = new Header("filename");
        this.underTest.handle(header, reader);
        assertEquals(NAME, header.getName());
    }
}
