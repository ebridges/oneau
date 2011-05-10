package com.oneau.data;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Date: Aug 17, 2010
 * Time: 3:17:56 PM
 */
public class DAOFactory {
	/*
    static final String JDBC_URL="jdbc:hsqldb:file:/Users/ebridges/Documents/Projects/1au/oneau-application/db/oneau-db";
    static final String JDBC_USERNAME="SA";
    static final String JDBC_PASSWORD="";
    static final String JDBC_DRIVER="org.hsqldb.jdbcDriver";
    */
    static final String JDBC_URL="jdbc:postgresql:oneau";
    static final String JDBC_USERNAME="postgres";
    static final String JDBC_PASSWORD="postgres";
    static final String JDBC_DRIVER="org.postgresql.Driver";

    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;
    private String jdbcDriver;

    private EphemerisDAO readOnlyDaoInstance;

    public static DAOFactory instance(String jdbcUrl, String jdbcUsername, String jdbcPassword, String jdbcDriver) {
        return new DAOFactory(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
    }

    public static DAOFactory instance() {
        return new DAOFactory();
    }

    private DAOFactory(String jdbcUrl, String jdbcUsername, String jdbcPassword, String jdbcDriver) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
        this.jdbcDriver = jdbcDriver;
        try {
            Class.forName(this.jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        this.readOnlyDaoInstance = new EphemerisDAOImpl(configureReadOnlyDataSource(jdbcUrl, jdbcUsername, jdbcPassword));
    }

    private DAOFactory() {
        this.jdbcUrl = JDBC_URL;
        this.jdbcUsername = JDBC_USERNAME;
        this.jdbcPassword = JDBC_PASSWORD;
        this.jdbcDriver = JDBC_DRIVER;
        try {
            Class.forName(this.jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        this.readOnlyDaoInstance = new EphemerisDAOImpl(configureReadOnlyDataSource(jdbcUrl, jdbcUsername, jdbcPassword));
    }

    public EphemerisDAO getEphemerisDAO() {
        return this.readOnlyDaoInstance;
    }

    private DataSource configureReadOnlyDataSource(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword) {
        return new DataSource() {
            public Connection getConnection() throws SQLException {
                Connection c = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
                c.setAutoCommit(true);
                c.setReadOnly(true);
                return c;
            }

            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(JDBC_URL, username, password);
            }

            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new SQLException("unwrapping not supported.");
            }

            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            public PrintWriter getLogWriter() throws SQLException {
                return new PrintWriter(System.out);
            }

            public void setLogWriter(PrintWriter out) throws SQLException {
                throw new IllegalArgumentException("logWriter cannot be set.");
            }

            public void setLoginTimeout(int seconds) throws SQLException {

            }

            public int getLoginTimeout() throws SQLException {
                return 0;
            }
        };
    }
}
