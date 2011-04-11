package com.oneau.loader.ephemeris;

import java.util.List;

import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

interface SqlGenerator {

	List<String> generateSchema();

	List<String> writeObservations(Observation observation);

	String writeRangeInfo(Observation observation);

	String writeFileInfo(Observation observation);

	String writeHeader(Header header);

}