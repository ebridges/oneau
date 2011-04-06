package com.oneau.test;

import static com.oneau.core.util.Utility.convertDate;
import static com.oneau.core.util.Utility.convertDouble;
import static com.oneau.core.util.Utility.convertInteger;
import static com.oneau.core.util.Utility.throwIfEmpty;

import java.util.Date;

import static com.oneau.core.util.Constants.TESTPO_DATE_PATTERN;;

public class EphemerisTestCase {
	private Integer id;
	private Integer ephemerisNumber;
	private Date ephemerisDate;
	private Double julianDate;
	private Integer targetBody;
	private Integer centerBody;
	private Integer coordinateId;
	private Double coordinate;
	private String row;
	
	public EphemerisTestCase(Integer id, String row) {
		this.id = id;
		this.row = row;
		this.initialize(row);
	}
	
	private void initialize(String row) {
		throwIfEmpty("test data row", row);
		String[] data = row.split("\\s+");
		if(data.length != 7) {
			throw new IllegalArgumentException("row format is invalid: ["+row+"]");
		}
		
		this.ephemerisNumber = convertInteger(data[0]);
		this.ephemerisDate = convertDate(data[1], TESTPO_DATE_PATTERN);
		this.julianDate = convertDouble(data[2]);
		this.targetBody = convertInteger(data[3]);
		this.centerBody = convertInteger(data[4]);
		this.coordinateId = convertInteger(data[5]);
		this.coordinate = convertDouble(data[6]);
	}

	public Integer getId() {
		return id;
	}
	
	public Integer getEphemerisNumber() {
		return ephemerisNumber;
	}

	public Date getEphemerisDate() {
		return ephemerisDate;
	}

	public Double getJulianDate() {
		return julianDate;
	}

	public Integer getTargetBody() {
		return targetBody;
	}

	public Integer getCenterBody() {
		return centerBody;
	}

	public Integer getCoordinateId() {
		return coordinateId;
	}

	public Double getCoordinate() {
		return coordinate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * this.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EphemerisTestCase)) {
			return false;
		}
		EphemerisTestCase other = (EphemerisTestCase) obj;
		return this.getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "EphemerisTestCase [id="+id+", ephemerisNumber=" + ephemerisNumber
				+ ", ephemerisDate=" + ephemerisDate + ", coordinate="
				+ coordinate + "]::["+row+"]";
	}
}
