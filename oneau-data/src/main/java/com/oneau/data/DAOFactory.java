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
    private static final String JDBC_URL="jdbc:hsqldb:file:/Users/ebridges/Documents/Projects/1au/oneau-application/db/oneau-db";
    private static final String JDBC_USERNAME="SA";
    private static final String JDBC_PASSWORD="";
    private static final String JDBC_DRIVER="org.hsqldb.jdbcDriver";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
    public static DAOFactory instance() {
        return new DAOFactory();
    }

    private DAOFactory() {
    }

    public EphemerisDAO getEphemerisDAO() {
        return new EphemerisDAOImpl(configureReadOnlyDataSource());
    }

    private DataSource configureReadOnlyDataSource() {
        return new DataSource() {
            public Connection getConnection() throws SQLException {
                Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
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
