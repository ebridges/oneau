package com.oneau.loader.ephemeris;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class PsqlGenerator extends AbstractSqlGenerator {

	@Override
	public List<String> generateSchema() {
        return unmodifiableList(
                asList(
                		"DROP SCHEMA IF EXISTS ONEAU CASCADE" + getStatementTerminator(),
                		/*
                		"DROP TABLE IF EXISTS ONEAU.OBSERVATION CASCADE" + getStatementTerminator(),
                		"DROP TABLE IF EXISTS ONEAU.EPHEMERIS_INTERVAL CASCADE" + getStatementTerminator(),
                		"DROP TABLE IF EXISTS ONEAU.MEASURED_ITEM CASCADE" + getStatementTerminator(),
                		"DROP TABLE IF EXISTS ONEAU.EPHEMERIS_DATA CASCADE" + getStatementTerminator(),
                		"DROP TABLE IF EXISTS ONEAU.CONSTANT CASCADE" + getStatementTerminator(),
                		"DROP TABLE IF EXISTS ONEAU.EPHEMERIS_HEADER CASCADE" + getStatementTerminator(),
                		*/
                		"DROP USER IF EXISTS ONEAU" + getStatementTerminator(),
                		"CREATE USER ONEAU WITH PASSWORD 'ONEAU'" + getStatementTerminator(),
                        "CREATE SCHEMA ONEAU AUTHORIZATION ONEAU" + getStatementTerminator(),                		
                		"CREATE TABLE ONEAU.EPHEMERIS_HEADER(id SERIAL,NAME TEXT NOT NULL UNIQUE,FILENAME TEXT NOT NULL UNIQUE,KSIZE INTEGER NOT NULL,NUM_COEFF INTEGER NOT NULL,EPOCH_START NUMERIC NOT NULL,EPOCH_END NUMERIC NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"CREATE TABLE ONEAU.CONSTANT(id SERIAL,HEADER_ID INTEGER NOT NULL,NAME TEXT NOT NULL,VALUE TEXT NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"CREATE TABLE ONEAU.EPHEMERIS_DATA(id SERIAL,FILENAME TEXT NOT NULL UNIQUE,HEADER_ID INTEGER NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"CREATE TABLE ONEAU.MEASURED_ITEM(id SERIAL,NAME TEXT NOT NULL UNIQUE,DIMENSIONS INTEGER NOT NULL,CHEB_COEFFS INTEGER NOT NULL,COEFF_SETS INTEGER NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"CREATE TABLE ONEAU.EPHEMERIS_INTERVAL(id SERIAL,RANGE_FROM NUMERIC NOT NULL,RANGE_TO NUMERIC NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"CREATE TABLE ONEAU.OBSERVATION(id SERIAL,FILE_ID INTEGER NOT NULL,MEASURED_ITEM_ID INTEGER NOT NULL,OBSERVATION_NUM INTEGER NOT NULL,INTERVAL_ID INTEGER,COEFFICIENT NUMERIC NOT NULL,PRIMARY KEY (id))" + getStatementTerminator(),
                		"ALTER TABLE ONEAU.CONSTANT ADD FOREIGN KEY (HEADER_ID) REFERENCES ONEAU.EPHEMERIS_HEADER (id)" + getStatementTerminator(),
                		"ALTER TABLE ONEAU.EPHEMERIS_DATA ADD FOREIGN KEY (HEADER_ID) REFERENCES ONEAU.EPHEMERIS_HEADER (id)" + getStatementTerminator(),
                		"ALTER TABLE ONEAU.OBSERVATION ADD FOREIGN KEY (FILE_ID) REFERENCES ONEAU.EPHEMERIS_DATA (id)" + getStatementTerminator(),
                		"ALTER TABLE ONEAU.OBSERVATION ADD FOREIGN KEY (MEASURED_ITEM_ID) REFERENCES ONEAU.MEASURED_ITEM (id)" + getStatementTerminator(),
                		"ALTER TABLE ONEAU.OBSERVATION ADD FOREIGN KEY (INTERVAL_ID) REFERENCES ONEAU.EPHEMERIS_INTERVAL (id) ON DELETE CASCADE" + getStatementTerminator()
                )
        );
	}

	@Override
	protected String getStatementTerminator() {
		return ";\n";
	}

}
