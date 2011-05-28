package com.oneau.loader.ephemeris;

import java.util.List;

import static com.oneau.core.util.Constants.COEFF_PRECISION;
import static com.oneau.core.util.Constants.COEFF_SCALE;
import static com.oneau.core.util.Constants.DATE_PRECISION;
import static com.oneau.core.util.Constants.DATE_SCALE;
import static com.oneau.data.SchemaObjectDefinition.CONSTANT;
import static com.oneau.data.SchemaObjectDefinition.EPHEMERIS_DATA;
import static com.oneau.data.SchemaObjectDefinition.EPHEMERIS_HEADER;
import static com.oneau.data.SchemaObjectDefinition.EPHEMERIS_INTERVAL;
import static com.oneau.data.SchemaObjectDefinition.MEASURED_ITEM;
import static com.oneau.data.SchemaObjectDefinition.OBSERVATION;
import static com.oneau.data.SchemaObjectDefinition.SCHEMANAME;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class HsqlGenerator extends AbstractSqlGenerator {

    /* (non-Javadoc)
	 * @see com.oneau.loader.ephemeris.SqlGenerator#generateSchema()
	 */
    @Override
	public List<String> generateSchema() {
        return unmodifiableList(
                asList(
                       format("DROP SCHEMA %s IF EXISTS CASCADE%s", SCHEMANAME, eol()),
                       format("CREATE SCHEMA %s AUTHORIZATION DBA%s", SCHEMANAME, eol()),
                       format("CREATE MEMORY TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,NAME VARCHAR(64) NOT NULL,FILENAME VARCHAR(64) NOT NULL,KSIZE INTEGER NOT NULL,NUM_COEFF INTEGER NOT NULL,EPOCH_START NUMERIC(%d,%d) NOT NULL,EPOCH_END NUMERIC(%d,%d) NOT NULL,CONSTRAINT SYS_CT_46 UNIQUE(NAME),CONSTRAINT SYS_CT_47 UNIQUE(FILENAME)) %s", SCHEMANAME, EPHEMERIS_HEADER, DATE_PRECISION, DATE_SCALE, DATE_PRECISION, DATE_SCALE, eol()),
                       format("CREATE MEMORY TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,FILENAME VARCHAR(64) NOT NULL,HEADER_ID INTEGER NOT NULL,CONSTRAINT SYS_CT_52 UNIQUE(HEADER_ID,FILENAME),CONSTRAINT SYS_FK_53 FOREIGN KEY(HEADER_ID) REFERENCES EPHEMERIS_HEADER(ID)) %s", SCHEMANAME, EPHEMERIS_DATA, eol()),
                       format("CREATE MEMORY TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,RANGE_FROM NUMERIC(%d,%d) NOT NULL,RANGE_TO NUMERIC(%d,%d) NOT NULL,CONSTRAINT SYS_CT_59 UNIQUE(RANGE_FROM,RANGE_TO)) %s", SCHEMANAME, EPHEMERIS_INTERVAL, DATE_PRECISION, DATE_SCALE, DATE_PRECISION, DATE_SCALE, eol()),
                       format("CREATE MEMORY TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,NAME VARCHAR(64) NOT NULL,DIMENSIONS INTEGER NOT NULL,CHEB_COEFFS INTEGER NOT NULL,COEFF_SETS INTEGER NOT NULL,CONSTRAINT SYS_CT_63 UNIQUE(NAME)) %s", SCHEMANAME, MEASURED_ITEM, eol()),
                       format("CREATE MEMORY TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,HEADER_ID INTEGER NOT NULL,NAME VARCHAR(64) NOT NULL,VALUE VARCHAR(64) NOT NULL,CONSTRAINT SYS_FK_67 FOREIGN KEY(HEADER_ID) REFERENCES EPHEMERIS_HEADER(ID),CONSTRAINT SYS_CT_68 UNIQUE(HEADER_ID,NAME))%s", SCHEMANAME, CONSTANT, eol()),
                       format("CREATE CACHED TABLE %s.%s(ID INTEGER NOT NULL PRIMARY KEY,FILE_ID INTEGER NOT NULL,MEASURED_ITEM_ID INTEGER NOT NULL,OBSERVATION_NUM INTEGER NOT NULL,INTERVAL_ID INTEGER,COEFFICIENT NUMERIC(%d,%d) NOT NULL,CONSTRAINT SYS_FK_74 FOREIGN KEY(FILE_ID) REFERENCES EPHEMERIS_DATA(ID),CONSTRAINT SYS_FK_75 FOREIGN KEY(MEASURED_ITEM_ID) REFERENCES MEASURED_ITEM(ID),CONSTRAINT SYS_FK_76 FOREIGN KEY(INTERVAL_ID) REFERENCES EPHEMERIS_INTERVAL(ID)) %s", SCHEMANAME, OBSERVATION, COEFF_PRECISION, COEFF_SCALE, eol()),
                       format("SET SCHEMA %s %s", SCHEMANAME, eol()),
					   format("SET FILES CACHE SIZE 60000 %s", eol()),
					   format("SET FILES CACHE ROWS 1000000 %s", eol()),
					   format("CREATE INDEX OBSERVATION_INDEX ON %s.%s (interval_id, measured_item_id, file_id) %s", SCHEMANAME, OBSERVATION, eol()),
					   format("CREATE INDEX INTERVAL_INDEX ON %s.%s (range_from, range_to) %s", SCHEMANAME, EPHEMERIS_INTERVAL, eol())
                )
        );
    }

	@Override
	protected String eol() {
		return "\n";
	}

}
