package com.oneau.loader.ephemeris;

public final class SqlGeneratorFactory {
    private static final String HSQL2_2 = "hsql database engine/2.2.1";
    private static final String HSQL2_0 = "hsql database engine/2.0.0";
    private static final String HSQL1_8 = "hsql database engine/1.8.0";
    private static final String PSQL9_0 = "postgresql/9.0.3";

    public static final String HSQL = HSQL2_2;
    public static final String PSQL = PSQL9_0;


	public static SqlGenerator instance(String dbtype) {
		if(dbtype.equals(HSQL)) {
			return new HsqlGenerator();
		} else if (dbtype.equals(PSQL)) {
			return new PsqlGenerator();
		} else {
			throw new IllegalArgumentException("unrecognized dbtype: "+dbtype);
		}
	}

	public static String getDbTypeKey(String databaseProductName, String databaseProductVersion) {
		return databaseProductName.toLowerCase() + "/" + databaseProductVersion.toLowerCase();
	}
}
