package com.oneau.loader.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.parser.ephemeris.CoefficientInfo;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * User: ebridges
 * Date: Nov 13, 2010
 */
public class SqlGenerator {
    private static final String INSERT_OBSERVATION = "INSERT INTO ONEAU.OBSERVATION (ID, FILE_ID, MEASURED_ITEM_ID, OBSERVATION_NUM, INTERVAL_ID, COEFFICIENT) VALUES (%d, %d, %d, %d, %d, %f)\n";
    private static final String INSERT_RANGE = "INSERT INTO ONEAU.EPHEMERIS_INTERVAL(ID, RANGE_FROM, RANGE_TO) VALUES (%d, %f, %f)\n";
    private static final String INSERT_FILE = "INSERT INTO ONEAU.EPHEMERIS_DATA(ID, FILENAME, HEADER_ID) VALUES (%d, '%s', %d)\n";
    private static final String INSERT_HEADER = "INSERT INTO ONEAU.EPHEMERIS_HEADER (ID, NAME, FILENAME, KSIZE, NUM_COEFF, EPOCH_START, EPOCH_END) VALUES (%d, '%s', '%s', %d, %d, %f, %f)\n";
    private static final String INSERT_CONSTANT = "INSERT INTO ONEAU.CONSTANT (ID, HEADER_ID, NAME, VALUE) VALUES (%d, %d, '%s', %f)\n";
    private static final String INSERT_BODY = "INSERT INTO ONEAU.MEASURED_ITEM (ID, NAME, DIMENSIONS, CHEB_COEFFS, COEFF_SETS) VALUES (%d, '%s', %d, %d, %d)\n";

    private long seq;
    private Long headerId;
    private Map<String, Long> fileIds;
    private Map<Range<Double>, Long> rangeIds;

    public SqlGenerator() {
        seq = 100L;
        this.headerId = null;
        this.fileIds = new HashMap<String, Long>();
        this.rangeIds = new HashMap<Range<Double>, Long>();
    }

    public List<String> generateSchema() {
        return unmodifiableList(
                asList(
//                       "CREATE SCHEMA PUBLIC AUTHORIZATION DBA",
                       "CREATE SCHEMA ONEAU AUTHORIZATION DBA",
                       "CREATE MEMORY TABLE ONEAU.EPHEMERIS_HEADER(ID INTEGER NOT NULL PRIMARY KEY,NAME VARCHAR(64) NOT NULL,FILENAME VARCHAR(64) NOT NULL,KSIZE INTEGER NOT NULL,NUM_COEFF INTEGER NOT NULL,EPOCH_START NUMERIC NOT NULL,EPOCH_END NUMERIC NOT NULL,CONSTRAINT SYS_CT_46 UNIQUE(NAME),CONSTRAINT SYS_CT_47 UNIQUE(FILENAME))",
                       "CREATE MEMORY TABLE ONEAU.EPHEMERIS_DATA(ID INTEGER NOT NULL PRIMARY KEY,FILENAME VARCHAR(64) NOT NULL,HEADER_ID INTEGER NOT NULL,CONSTRAINT SYS_CT_52 UNIQUE(HEADER_ID,FILENAME),CONSTRAINT SYS_FK_53 FOREIGN KEY(HEADER_ID) REFERENCES EPHEMERIS_HEADER(ID))",
                       "CREATE MEMORY TABLE ONEAU.EPHEMERIS_INTERVAL(ID INTEGER NOT NULL PRIMARY KEY,RANGE_FROM NUMERIC NOT NULL,RANGE_TO NUMERIC NOT NULL,CONSTRAINT SYS_CT_59 UNIQUE(RANGE_FROM,RANGE_TO))",
                       "CREATE MEMORY TABLE ONEAU.MEASURED_ITEM(ID INTEGER NOT NULL PRIMARY KEY,NAME VARCHAR(64) NOT NULL,DIMENSIONS INTEGER NOT NULL,CHEB_COEFFS INTEGER NOT NULL,COEFF_SETS INTEGER NOT NULL,CONSTRAINT SYS_CT_63 UNIQUE(NAME))",
                       "CREATE MEMORY TABLE ONEAU.CONSTANT(ID INTEGER NOT NULL PRIMARY KEY,HEADER_ID INTEGER NOT NULL,NAME VARCHAR(64) NOT NULL,VALUE VARCHAR(64) NOT NULL,CONSTRAINT SYS_FK_67 FOREIGN KEY(HEADER_ID) REFERENCES EPHEMERIS_HEADER(ID),CONSTRAINT SYS_CT_68 UNIQUE(HEADER_ID,NAME))",
                       "CREATE CACHED TABLE ONEAU.OBSERVATION(ID INTEGER NOT NULL PRIMARY KEY,FILE_ID INTEGER NOT NULL,MEASURED_ITEM_ID INTEGER NOT NULL,OBSERVATION_NUM INTEGER NOT NULL,INTERVAL_ID INTEGER,COEFFICIENT NUMERIC NOT NULL,CONSTRAINT SYS_FK_74 FOREIGN KEY(FILE_ID) REFERENCES EPHEMERIS_DATA(ID),CONSTRAINT SYS_FK_75 FOREIGN KEY(MEASURED_ITEM_ID) REFERENCES MEASURED_ITEM(ID),CONSTRAINT SYS_FK_76 FOREIGN KEY(INTERVAL_ID) REFERENCES EPHEMERIS_INTERVAL(ID))",
                       "SET TABLE ONEAU.OBSERVATION INDEX'56621544 56621544 32 56621544 0'",
//                       "CREATE USER SA PASSWORD ''",
                       "CREATE USER ONEAU PASSWORD 'ONEAU'",
//                       "GRANT DBA TO SA",
                       "SET WRITE_DELAY 10",
                       "SET SCHEMA ONEAU"
                )
        );
    }

    public List<String> writeObservations(Observation observation) {
        List<String> sql = new ArrayList<String>(observation.getCoefficients().values().size()*HeavenlyBody.values().length);

        Long fileId = this.fileIds.get(observation.getFilename());
        if(null == fileId) {
            throw new IllegalArgumentException("unable to get fileId for "+observation.getFilename());
        }

        Long rangeId = this.rangeIds.get(observation.getBeginEndDates());
        if(null == rangeId) {
            throw new IllegalArgumentException("unable to get rangeId for "+observation.getBeginEndDates());
        }

        for(HeavenlyBody body : observation.getCoefficients().keySet()) {
            for(Double coefficient : observation.getCoefficients().get(body)) {
                sql.add(format(
                        INSERT_OBSERVATION,
                        seq++,
                        fileId,
                        body.getId(),
                        observation.getObservationNumber(),
                        rangeId,
                        coefficient
                ));
            }
        }
        
        return unmodifiableList(sql);
    }

    public String writeRangeInfo(Observation observation) {
        if(! rangeIds.containsKey(observation.getBeginEndDates())) {
            if(null == observation.getBeginEndDates()) {
                throw new NullPointerException("range cannot be null");
            }
            StringBuilder out = new StringBuilder(INSERT_RANGE.length() + 24*3);
            long intervalId = seq++;
            out.append(format(INSERT_RANGE, intervalId, observation.getBeginEndDates().getLeft(), observation.getBeginEndDates().getRight()));
            this.rangeIds.put(observation.getBeginEndDates(), intervalId);
            return out.toString();
        } else {
            return null;
        }
    }

    public String writeFileInfo(Observation observation) {
        if(! fileIds.containsKey(observation.getFilename())) {
            StringBuilder out = new StringBuilder(INSERT_FILE.length() + 24*2 + observation.getFilename().length());
            long fileId = seq++;
            out.append(format(INSERT_FILE, fileId, observation.getFilename(), headerId));
            this.fileIds.put(observation.getFilename(), fileId);
            return out.toString();
        } else {
            return null;
        }
    }

    public String writeHeader(Header header) {
        if(null == headerId) {
            StringBuilder out = new StringBuilder(sizeInsertHeader(header));
            headerId = seq++;
            out.append(format(INSERT_HEADER, headerId, header.getName(), header.getFilename(), header.getKsize(), header.getNumCoeff(), header.getStartEpoch(), header.getEndEpoch()));
            writeBodies(out, header.getCoeffInfo());
            writeConstants(out, header.getConstantNames(), header.getConstantValues());
            return out.toString();
        } else {
            return null;
        }
    }

    private void writeConstants(StringBuilder out, List<String> names, List<Double> values) {
        int sz = names.size();
        for(int i=0; i<sz; i++) {
            out.append(format(INSERT_CONSTANT, seq++, headerId, names.get(i), values.get(i)));
        }
    }

    private void writeBodies(StringBuilder out, Map<HeavenlyBody, CoefficientInfo> coeffInfo) {
        for(HeavenlyBody body : coeffInfo.keySet()) {
            out.append(format(INSERT_BODY, body.getId(), body.getName(), body.getDimensions(), coeffInfo.get(body).getCoeffCount(), coeffInfo.get(body).getCoeffSets()));
        }
    }

    private int sizeInsertHeader(Header header) {
        int sz = 100;
        sz += HeavenlyBody.values().length * INSERT_BODY.length();
        sz += header.getConstantNames().size() * INSERT_CONSTANT.length();
        return sz;
    }
}
