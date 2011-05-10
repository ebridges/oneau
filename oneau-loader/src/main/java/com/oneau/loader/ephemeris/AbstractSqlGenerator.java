package com.oneau.loader.ephemeris;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.Range;
import com.oneau.parser.ephemeris.CoefficientInfo;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

/**
 * User: ebridges
 * Date: Nov 13, 2010
 */
abstract class AbstractSqlGenerator implements SqlGenerator {
    private static final Logger logger = Logger.getLogger(AbstractSqlGenerator.class.getName());
    private long seq;
    private Long headerId;
    private Map<String, Long> fileIds;
    private Map<Range<BigDecimal>, Long> rangeIds;

    public AbstractSqlGenerator() {
        seq = 100L;
        this.headerId = null;
        this.fileIds = new HashMap<String, Long>();
        this.rangeIds = new HashMap<Range<BigDecimal>, Long>();
    }

    protected abstract String getStatementTerminator();
    
    protected String generateInsertObservation() {
    	return "INSERT INTO ONEAU.OBSERVATION (ID, FILE_ID, MEASURED_ITEM_ID, OBSERVATION_NUM, INTERVAL_ID, COEFFICIENT) VALUES (%d, %d, %d, %d, %d, %32.17f)" + getStatementTerminator();   	
    }
    
    protected String generateInsertRange(){
    	return "INSERT INTO ONEAU.EPHEMERIS_INTERVAL(ID, RANGE_FROM, RANGE_TO) VALUES (%d, %f, %f)" + getStatementTerminator();
    }
    
    protected String generateInsertFile(){
    	return "INSERT INTO ONEAU.EPHEMERIS_DATA(ID, FILENAME, HEADER_ID) VALUES (%d, '%s', %d)" + getStatementTerminator();
    }
    
    protected String generateInsertHeader(){
    	return "INSERT INTO ONEAU.EPHEMERIS_HEADER (ID, NAME, FILENAME, KSIZE, NUM_COEFF, EPOCH_START, EPOCH_END) VALUES (%d, '%s', '%s', %d, %d, %f, %f)" + getStatementTerminator();
    }
    
    protected String generateInsertConstant(){
    	return "INSERT INTO ONEAU.CONSTANT (ID, HEADER_ID, NAME, VALUE) VALUES (%d, %d, '%s', %f)" + getStatementTerminator();
    }
    
    protected String generateInsertBody(){
    	return "INSERT INTO ONEAU.MEASURED_ITEM (ID, NAME, DIMENSIONS, CHEB_COEFFS, COEFF_SETS) VALUES (%d, '%s', %d, %d, %d)" + getStatementTerminator();
    }
    
    /* (non-Javadoc)
	 * @see com.oneau.loader.ephemeris.SqlGenerator#writeObservations(com.oneau.parser.ephemeris.Observation)
	 */
    @Override
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
            for(BigDecimal coefficient : observation.getCoefficients().get(body)) {
                sql.add(format(
                        this.generateInsertObservation(),
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

    /* (non-Javadoc)
	 * @see com.oneau.loader.ephemeris.SqlGenerator#writeRangeInfo(com.oneau.parser.ephemeris.Observation)
	 */
    @Override
	public String writeRangeInfo(Observation observation) {
        if(! rangeIds.containsKey(observation.getBeginEndDates())) {
            if(null == observation.getBeginEndDates()) {
                throw new NullPointerException("range cannot be null");
            }
            StringBuilder out = new StringBuilder(this.generateInsertRange().length() + 24*3);
            long intervalId = seq++;
            out.append(format(this.generateInsertRange(), intervalId, observation.getBeginEndDates().getLeft(), observation.getBeginEndDates().getRight()));
            this.rangeIds.put(observation.getBeginEndDates(), intervalId);
            return out.toString();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
	 * @see com.oneau.loader.ephemeris.SqlGenerator#writeFileInfo(com.oneau.parser.ephemeris.Observation)
	 */
    @Override
	public String writeFileInfo(Observation observation) {
        if(! fileIds.containsKey(observation.getFilename())) {
            StringBuilder out = new StringBuilder(this.generateInsertFile().length() + 24*2 + observation.getFilename().length());
            long fileId = seq++;
            out.append(format(this.generateInsertFile(), fileId, observation.getFilename(), headerId));
            this.fileIds.put(observation.getFilename(), fileId);
            return out.toString();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
	 * @see com.oneau.loader.ephemeris.SqlGenerator#writeHeader(com.oneau.parser.ephemeris.Header)
	 */
    @Override
	public String writeHeader(Header header) {
        if(null == headerId) {
            StringBuilder out = new StringBuilder(sizeInsertHeader(header));
            headerId = seq++;
            out.append(format(this.generateInsertHeader(), headerId, header.getName(), header.getFilename(), header.getKsize(), header.getNumCoeff(), header.getStartEpoch(), header.getEndEpoch()));
            writeBodies(out, header.getCoeffInfo());
            writeConstants(out, header.getConstantNames(), header.getConstantValues());
            logger.finer(format("Header SQL:\n%s", out.toString()));
            return out.toString();
        } else {
            return null;
        }
    }

    private void writeConstants(StringBuilder out, List<String> names, List<BigDecimal> values) {
        int sz = names.size();
        for(int i=0; i<sz; i++) {
            out.append(format(this.generateInsertConstant(), seq++, headerId, names.get(i), values.get(i)));
        }
    }

    private void writeBodies(StringBuilder out, Map<HeavenlyBody, CoefficientInfo> coeffInfo) {
        for(HeavenlyBody body : coeffInfo.keySet()) {
            out.append(format(this.generateInsertBody(), body.getId(), body.getName(), body.getDimensions(), coeffInfo.get(body).getCoeffCount(), coeffInfo.get(body).getCoeffSets()));
        }
    }

    private int sizeInsertHeader(Header header) {
        int sz = 100;
        sz += HeavenlyBody.values().length * this.generateInsertBody().length();
        sz += header.getConstantNames().size() * this.generateInsertConstant().length();
        return sz;
    }
}
