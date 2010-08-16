package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.parser.ephemeris.CoefficientInfo;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Aug 13, 2010
 */
public class SqlObservationWriter implements ObservationWriter {
    private static final Logger logger = Logger.getLogger(SqlObservationWriter.class.getName());
    private long seq;
    private Writer out;
    private Long headerId;
    private Map<String, Long> fileIds;
    private Map<Range<Double>, Long> rangeIds;

    public SqlObservationWriter(Writer w) {
        out = w;
        seq = 100L;
        this.fileIds = new HashMap<String, Long>();
        this.rangeIds = new HashMap<Range<Double>, Long>();
    }

    @Override
    public void write(Header header, Observation observation) {
        logger.fine("write() called");
        try {
            if(null == headerId) {
                writeHeader(header);
            }
            if(! fileIds.containsKey(observation.getFilename())) {
                writeFileInfo(observation);
            }
            if(! rangeIds.containsKey(observation.getBeginEndDates())) {
                writeRangeInfo(observation.getBeginEndDates());
            }
            writeObservations(observation);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void writeObservations(Observation observation) throws IOException {
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
                out.write(format(
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
    }

    private void writeRangeInfo(Range<Double> range) throws IOException {
        long intervalId = seq++;
        out.write(format(INSERT_RANGE, intervalId, range.getLeft(), range.getRight()));
        this.rangeIds.put(range, intervalId);
    }

    private void writeFileInfo(Observation observation) throws IOException {
        long fileId = seq++;
        out.write(format(INSERT_FILE, fileId, observation.getFilename(), headerId));
        this.fileIds.put(observation.getFilename(), fileId);

    }

    private void writeHeader(Header header) throws IOException {
        headerId = seq++;
        out.write(format(INSERT_HEADER, headerId, header.getName(), header.getFilename(), header.getKsize(), header.getNumCoeff(), header.getStartEpoch(), header.getEndEpoch()));
        writeBodies(header.getCoeffInfo());
        writeConstants(header.getConstantNames(), header.getConstantValues());
    }

    private void writeConstants(List<String> names, List<Double> values) throws IOException {
        int sz = names.size();
        for(int i=0; i<sz; i++) {
            out.write(format(INSERT_CONSTANT, seq++, headerId, names.get(i), values.get(i)));
        }
    }

    private void writeBodies(Map<HeavenlyBody, CoefficientInfo> coeffInfo) throws IOException {
        for(HeavenlyBody body : coeffInfo.keySet()) {
            out.write(format(INSERT_BODY, body.getId(), body.getName(), body.getDimensions(), coeffInfo.get(body).getCoeffCount(), coeffInfo.get(body).getCoeffSets()));
        }
    }

    private static final String INSERT_HEADER = "INSERT INTO ONEAU.EPHEMERIS_HEADER (ID, NAME, FILENAME, KSIZE, NUM_COEFF, EPOCH_START, EPOCH_END) VALUES (%d, '%s', '%s', %d, %d, %f, %f)\n";
    private static final String INSERT_CONSTANT = "INSERT INTO ONEAU.CONSTANT (ID, HEADER_ID, NAME, VALUE) VALUES (%d, %d, '%s', %f)\n";
    private static final String INSERT_FILE = "INSERT INTO ONEAU.EPHEMERIS_DATA(ID, FILENAME, HEADER_ID) VALUES (%d, '%s', %d)\n";
    private static final String INSERT_RANGE = "INSERT INTO ONEAU.EPHEMERIS_INTERVAL(ID, RANGE_FROM, RANGE_TO) VALUES (%d, %f, %f)\n";
    private static final String INSERT_BODY = "INSERT INTO ONEAU.MEASURED_ITEM (ID, NAME, DIMENSIONS, CHEB_COEFFS, COEFF_SETS) VALUES (%d, '%s', %d, %d, %d)\n";
    private static final String INSERT_OBSERVATION = "INSERT INTO ONEAU.OBSERVATION (ID, FILE_ID, MEASURED_ITEM_ID, OBSERVATION_NUM, INTERVAL_ID, COEFFICIENT) VALUES (%d, %d, %d, %d, %d, %f)\n";
}
