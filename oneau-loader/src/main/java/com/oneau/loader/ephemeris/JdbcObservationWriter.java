package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import static com.oneau.core.util.Utility.isEmpty;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Nov 13, 2010
 */
public class JdbcObservationWriter implements ObservationWriter {
    private static final Logger logger = Logger.getLogger(JdbcObservationWriter.class.getName());
    private static final String URL_PROPERTY = "jdbcUrl";
    private static final String DRIVER_PROPERTY = "jdbcDriver";
    private static final String USERNAME_PROPERTY = "jdbcUsername";
    private static final String PASSWORD_PROPERTY = "jdbcPassword";

    private SqlGenerator sqlGenerator;
    private Connection connection;

    public JdbcObservationWriter() {
        this.sqlGenerator = new SqlGenerator();
    }

    @Override
    public void init() {
        try {
            Class.forName(getProperty(DRIVER_PROPERTY));
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            this.connection = initializeConnection();
            for(String sql : sqlGenerator.generateSchema())
                executeStatement(sql);
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void finish() {
        if(null != connection){
            try {
                connection.close();
            } catch (SQLException e) {
                //noinspection ThrowFromFinallyBlock
                throw new IllegalArgumentException("caught exception when closing connection", e);
            }
        }

    }

    @Override
    public void write(Header header, Observation observation) {
        logger.info("write() called");

        try {
            String headerInfo = sqlGenerator.writeHeader(header);
            if(null != headerInfo)
                executeStatement(headerInfo);

            String fileInfo = sqlGenerator.writeFileInfo(observation);
            if(null != fileInfo)
                executeStatement(fileInfo);

            String rangeInfo = sqlGenerator.writeRangeInfo(observation);
            if(null != rangeInfo)
                executeStatement(rangeInfo);

            for(String sql : sqlGenerator.writeObservations(observation))
                executeStatement(sql);

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void executeStatement(String sql) throws SQLException {
        int result = connection.createStatement().executeUpdate(sql);
        if(result != 1) {
            logger.warning(format("insert returned [%d] when executing [%s]", result, sql));
        }

    }

    private Connection initializeConnection() throws SQLException {
        return DriverManager.getConnection(
                getProperty(URL_PROPERTY),
                getProperty(USERNAME_PROPERTY),
                getProperty(PASSWORD_PROPERTY)
        );
    }

    private static String getProperty(String property) {
        String p = System.getProperty(property);
        if(isEmpty(p)) {
            logger.warning(format("%s is not set as a system property", property));
        }
        return (null != p ? p : "");
    }
}
