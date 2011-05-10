package com.oneau.parser.ephemeris;

import com.oneau.core.util.HeavenlyBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oneau.core.util.Utility.isEmpty;
import static com.oneau.core.util.Utility.parseCoefficient;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * User: ebridges
 * Date: Jul 20, 2010
 */
public class HeaderHandlerFactory {
    public static HeaderHandler getHandler(String line) {
        if(line.startsWith("KSIZE")) {
            return new FirstLineHandler(line);
        } else if(line.startsWith("GROUP")) {
            String[] fields = line.split("\\s+");
            return newGroupHandler(parseInt(fields[1]));
        } else {
            return new NoOpHeaderHandler();
        }
    }

    private static HeaderHandler newGroupHandler(int groupId) {
        switch(groupId) {
            case(1010):
                return new Group1010Handler();
            case(1030):
                return new Group1030Handler();
            case(1040):
                return new Group1040Handler();
            case(1041):
                return new Group1041Handler();
            case(1050):
                return new Group1050Handler();
            case(1070):
                return new NoOpHeaderHandler();
            default:
                throw new IllegalArgumentException(format("got invalid groupId [%d]",groupId));
        }

    }

    static class NoOpHeaderHandler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            // do nothing
        }
    }

    /**
     * e.g.
     * KSIZE=  2036    NCOEFF=  1018
     */
    static class FirstLineHandler implements HeaderHandler {
        private String firstLine;
        public FirstLineHandler(String line) {
            this.firstLine = line;
        }

        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            String[] fields = firstLine.split("\\s+");
            header.setKsize( parseInt(fields[1]) );
            header.setNumCoeff( parseInt(fields[3]) );
        }
    }

    /**
     * e.g.:
     * GROUP   1010
     *
     * JPL Planetary Ephemeris DE405/DE405
     * Start Epoch: JED=  2305424.5 1599 DEC 09 00:00:00
     * Final Epoch: JED=  2525008.5 2201 FEB 20 00:00:00
     *
     */
    static class Group1010Handler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            String line = null;
            while( (line = data.readLine()) != null ) {
                if(isEmpty(line)) {
                    continue;
                }
                if(line.startsWith("JPL")) {
                    header.setName(line.trim());
                    return;
                }
            }
        }
    }

    /**
     * e.g.:
     * GROUP   1030
     *
     *   2305424.50  2525008.50         32.
     *
     */
    static class Group1030Handler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            String line = null;
            while( (line = data.readLine()) != null ) {
                if(isEmpty(line)) {
                    continue;
                }
                if(line.startsWith("GROUP")) {
                    continue;
                }
                String[] fields = line.trim().split("\\s+");
                if(fields.length == 3) {
                    header.setStartEpoch( Double.parseDouble(fields[0]));
                    header.setEndEpoch( Double.parseDouble(fields[1]));
                    Double d = Double.parseDouble(fields[2]);
                    header.setDaysInInterval( d.intValue() );
                    return;
                }
            }
        }
    }

    /**
     * e.g.:
     * GROUP   1040
     *
     *    156
     *   DENUM   LENUM   TDATEF  TDATEB  CENTER  CLIGHT  AU      EMRAT   GM1     GM2
     *   GMB     GM4     GM5     GM6     GM7     GM8     GM9     GMS     RAD1    RAD2
     *   RAD4    JDEPOC  X1      Y1      Z1      XD1     YD1     ZD1     X2      Y2
     *   Z2      XD2     YD2     ZD2     XB      YB      ZB      XDB     YDB     ZDB
     *   X4      Y4      Z4      XD4     YD4     ZD4     X5      Y5      Z5      XD5
     *   YD5     ZD5     X6      Y6      Z6      XD6     YD6     ZD6     X7      Y7
     *   Z7      XD7     YD7     ZD7     X8      Y8      Z8      XD8     YD8     ZD8
     *   .
     *   .
     *   .
     */
    static class Group1040Handler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            String line = null;
            int constantsCount = 0;
            List<String> constantNames = new ArrayList<String>(175);
            while( (line = data.readLine()) != null ) {
                if(isEmpty(line)) {
                    continue;
                }
                if(line.startsWith("GROUP")) {
                    continue;
                }
                String[] fields = line.trim().split("\\s+");

                if(fields.length == 1) {
                    constantsCount = Integer.parseInt(fields[0]);
                    continue;
                }
                if(fields.length > 1) {
                    for(String name : fields) {
                        constantNames.add(name);
                    }
                    if(constantNames.size() == constantsCount) {
                        break;
                    }
                    continue;
                }
            }
            if(constantNames.size() != constantsCount) {
                throw new IllegalArgumentException(format("constantNames mismatch: expected [%d], actual [%d]", constantsCount, constantNames.size()));
            }
            header.setConstantNames(unmodifiableList(constantNames));
        }
    }

    /**
     * GROUP   1041
     *
     *   156
     * 0.405000000000000000D+03  0.405000000000000000D+03  0.000000000000000000D+00
     * 0.119970525194723000D+17  0.000000000000000000D+00  0.299792457999999984D+06
     * 0.149597870691000015D+09  0.813005600000000044D+02  0.491254745145081187D-10
     * .
     * .
     * .
     *
     */
    static class Group1041Handler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            int constantsCount = 0;
            List<BigDecimal> constantValues = new ArrayList<BigDecimal>(175);
            String line = null;
            while( (line = data.readLine()) != null ) {
                if(isEmpty(line)) {
                    continue;
                }
                if(line.startsWith("GROUP")) {
                    continue;
                }
                String[] fields = line.trim().split("\\s+");

                if(fields.length == 1) {
                    constantsCount = Integer.parseInt(fields[0]);
                    continue;
                }
                if(fields.length == 3) {
                    for(String val : fields) {
                        if(isEmpty(val)){
                            throw new IllegalArgumentException("got empty val!");
                        }
                        constantValues.add(parseCoefficient(val.trim()));
                    }
                    if(constantValues.size() == constantsCount) {
                        break;
                    }
                    continue;
                }
                break;
            }
            if(constantValues.size() != constantsCount) {
                throw new IllegalArgumentException(format("constantValues mismatch: expected [%d], actual [%d]", constantsCount, constantValues.size()));
            }
            header.setConstantValues(unmodifiableList(constantValues));
        }
    }

    /**
     * e.g.:
     * GROUP   1050
     *
     *      3   171   231   309   342   366   387   405   423   441   753   819   899
     *     14    10    13    11     8     7     6     6     6    13    11    10    10
     *      4     2     2     1     1     1     1     1     1     8     2     4     4
     *
     The roadmap to the contents of the 32-day or 64-day blocks is given by
      "pointers", contained in the first record of the binary files or in the
      "GROUP 1050" of the ascii headers. The pointers consist of 3 sets of 13
      integers each. (In the binary version, the first 12 members of the three sets
      are stored together; the 13th members of each set are stored later in the
      record.) The 13 triplets give information about the location, order and, time-
      coverage of the chebychev polynomials corresponding to the following 13 items:

         Mercury
         Venus
         Earth-Moon barycenter
         Mars
         Jupiter
         Saturn
         Uranus
         Neptune
         Pluto
         Moon (geocentric)
         Sun
         Nutations
         Librations

      For the ith item, pointer(1,i) is the starting location in each data record of
      the chebychev coefficients; pointer(2,i) is the number of coefficients per
      component; pointer(3,i) is the number of complete sets of coefficients in each
      data record.

       For example, the pointers for DE405 look like,

        3  171  231  309  342  366  387  405  423  441  753  819  899
       14  10  13  11   8   7   6   6   6  13  11  10  10
        4   2   2   1   1   1   1   1   1   8   2   4   4

       For the moon, starting in the 441st double precision word, there are 13
       coefficients for the x-coordinate which apply over the first 4 (32/8) days of
       the 32-day interval covered by this block of data. The next 13 coefficients
       are for the y-coordinate; then, 13 for z. Seven similar sets follow for the
       moon, making 13x3x8=312 words in all. The coefficients for the Sun follow
       the moon, starting in the 753rd location.

      There are three cartesian components (x, y, z), for each of the items #1-11;
      there are two components for the 12th item, nutations : d(psi) and d(epsilon);
      there are three components for the 13th item, librations : three euler angles.
      Velocites are obtained by interpolating the position polynomials.

     */
    static class Group1050Handler implements HeaderHandler {
        @Override
        public void handle(Header header, BufferedReader data) throws IOException {
            CoefficientInfo[] bodyList = new CoefficientInfo[13];
            for(HeavenlyBody body : HeavenlyBody.orderedByIndex()) {
                bodyList[body.getId()-1] = new CoefficientInfo(body);
            }
            int rowCount = 1;
            String line = null;
            while( (line = data.readLine()) != null ) {
                if(isEmpty(line)) {
                    continue;
                }
                if(line.startsWith("GROUP")) {
                    continue;
                }
                String[] fields = line.trim().split("\\s+");

                if(fields.length == 13) {
                    for(int i=0; i<13; i++) {
                        switch(rowCount) {
                            case(1):
                                bodyList[i].setFileStartIndex(parseInt(fields[i]));
                                break;
                            case(2):
                                bodyList[i].setCoeffCount(parseInt(fields[i]));
                                break;
                            case(3):
                                bodyList[i].setCoeffSets(parseInt(fields[i]));
                                break;
                            default:
                                throw new IllegalArgumentException("only 3 rows expected in group 1050");
                        }
                    }
                }
                rowCount++;                
            }

            Map<HeavenlyBody, CoefficientInfo> coefficientInfo = new HashMap<HeavenlyBody, CoefficientInfo>();
            for(CoefficientInfo i : bodyList) {
                coefficientInfo.put(
                        i.getBody(),
                        i
                );
            }
            header.setCoeffInfo(unmodifiableMap(coefficientInfo));
        }
    }
}
