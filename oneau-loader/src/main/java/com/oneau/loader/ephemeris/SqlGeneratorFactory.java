package com.oneau.loader.ephemeris;

public final class SqlGeneratorFactory {
	public static SqlGenerator instance(String dbtype) {
		if(dbtype.equals("hsql database engine/1.8.0")) {
			return new HsqlGenerator();
		} else if (dbtype.equals("postgresql/9.0.3")) {
			return new PsqlGenerator();
		} else {
			throw new IllegalArgumentException("unrecognized dbtype: "+dbtype);
		}
	}

	public static String getDbTypeKey(String databaseProductName, String databaseProductVersion) {
		return databaseProductName.toLowerCase() + "/" + databaseProductVersion.toLowerCase();
	}
}
