package com.oneau.loader.ephemeris;

import static com.oneau.loader.ephemeris.SchemaObjectDefinition.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class PsqlGenerator extends AbstractSqlGenerator {

	@Override
	public List<String> generateSchema() {
        return unmodifiableList(
                asList(
                		format("DROP SCHEMA IF EXISTS %s CASCADE %s", SCHEMANAME, eol()),
                		format("DROP USER IF EXISTS %s %s", SCHEMANAME, eol()),
                		format("CREATE USER %s WITH PASSWORD '%s' %s", SCHEMANAME, pwd(), eol()),
                        format("CREATE SCHEMA %s AUTHORIZATION %s %s", SCHEMANAME, SCHEMANAME,  eol()),

                		format("CREATE TABLE %s.%s(id SERIAL,NAME TEXT NOT NULL UNIQUE,FILENAME TEXT NOT NULL UNIQUE,KSIZE INTEGER NOT NULL,NUM_COEFF INTEGER NOT NULL,EPOCH_START NUMERIC NOT NULL,EPOCH_END NUMERIC NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, EPHEMERIS_HEADER, eol()),
                        format("CREATE TABLE %s.%s(id SERIAL,HEADER_ID INTEGER NOT NULL,NAME TEXT NOT NULL,VALUE TEXT NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, CONSTANT, eol()),
                		format("CREATE TABLE %s.%s(id SERIAL,FILENAME TEXT NOT NULL UNIQUE,HEADER_ID INTEGER NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, EPHEMERIS_DATA, eol()),
                		format("CREATE TABLE %s.%s(id SERIAL,NAME TEXT NOT NULL UNIQUE,DIMENSIONS INTEGER NOT NULL,CHEB_COEFFS INTEGER NOT NULL,COEFF_SETS INTEGER NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, MEASURED_ITEM, eol()),
                		format("CREATE TABLE %s.%s(id SERIAL,RANGE_FROM NUMERIC NOT NULL,RANGE_TO NUMERIC NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, EPHEMERIS_INTERVAL, eol()),
                		format("CREATE TABLE %s.%s(id SERIAL,FILE_ID INTEGER NOT NULL,MEASURED_ITEM_ID INTEGER NOT NULL,OBSERVATION_NUM INTEGER NOT NULL,INTERVAL_ID INTEGER,COEFFICIENT NUMERIC NOT NULL,PRIMARY KEY (id)) %s",  SCHEMANAME, OBSERVATION, eol()),

                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, EPHEMERIS_HEADER, SCHEMANAME, eol()),
                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, CONSTANT, SCHEMANAME, eol()),
                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, EPHEMERIS_DATA, SCHEMANAME, eol()),
                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, MEASURED_ITEM, SCHEMANAME, eol()),
                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, EPHEMERIS_INTERVAL, SCHEMANAME, eol()),
                        format("ALTER TABLE %s.%s OWNER TO %s %s", SCHEMANAME, OBSERVATION, SCHEMANAME, eol()),

                		format("ALTER TABLE %s.%s ADD FOREIGN KEY (HEADER_ID) REFERENCES %s.%s (id) %s", SCHEMANAME, CONSTANT, SCHEMANAME, EPHEMERIS_HEADER, eol()),
                		format("ALTER TABLE %s.%s ADD FOREIGN KEY (HEADER_ID) REFERENCES %s.%s (id) %s", SCHEMANAME, EPHEMERIS_DATA, SCHEMANAME, EPHEMERIS_HEADER, eol()),
                		format("ALTER TABLE %s.%s ADD FOREIGN KEY (FILE_ID) REFERENCES %s.%s (id) %s", SCHEMANAME, OBSERVATION, SCHEMANAME, EPHEMERIS_DATA, eol()),
                		format("ALTER TABLE %s.%s ADD FOREIGN KEY (MEASURED_ITEM_ID) REFERENCES %s.%s (id) %s", SCHEMANAME, OBSERVATION, SCHEMANAME, MEASURED_ITEM, eol()),
                		format("ALTER TABLE %s.%s ADD FOREIGN KEY (INTERVAL_ID) REFERENCES %s.%s (id) ON DELETE CASCADE %s", SCHEMANAME, OBSERVATION, SCHEMANAME, EPHEMERIS_INTERVAL, eol())
                )
        );
	}

	@Override
	protected String eol() {
		return ";\n";
	}

}
