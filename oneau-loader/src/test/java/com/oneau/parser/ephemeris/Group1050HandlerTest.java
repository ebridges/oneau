package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * User: ebridges
 * Date: Jul 21, 2010
 */
public class Group1050HandlerTest {

    private static final int[] EXPECTED_VALUES = {
            3,14,4,171,10,2,231,13,2,309,11,1,342,8,1,366,7,1,387,6,1,
            405,6,1,423,6,1,441,13,8,753,11,2,819,10,4,899,10,4
    };

    private static final String HEADER1050 = "GROUP   1050\n" +
            " \n" +
            "     3   171   231   309   342   366   387   405   423   441   753   819   899\n" +
            "    14    10    13    11     8     7     6     6     6    13    11    10    10\n" +
            "     4     2     2     1     1     1     1     1     1     8     2     4     4";


    private HeaderHandlerFactory.Group1050Handler underTest;

    public Group1050HandlerTest() {
        this.underTest = new HeaderHandlerFactory.Group1050Handler();
    }

    @Test
    public void testGroupParsing() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(HEADER1050));
        Header header = new Header("filename");
        this.underTest.handle(header, reader);
        Map<HeavenlyBody, CoefficientInfo> coeffInfo = header.getCoeffInfo() ;

        int i=0;
        for(HeavenlyBody body : HeavenlyBody.orderedByIndex()) {
            CoefficientInfo nfo = coeffInfo.get(body);
            System.out.println(nfo);
            assertEquals(EXPECTED_VALUES[i++], (int)nfo.getStartIndex());
            assertEquals(EXPECTED_VALUES[i++], (int)nfo.getCoeffCount());
            assertEquals(EXPECTED_VALUES[i++], (int)nfo.getCoeffSets());
        }
    }
}
