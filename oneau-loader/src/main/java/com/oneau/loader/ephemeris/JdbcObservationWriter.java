package com.oneau.loader.ephemeris;

import com.oneau.common.ObservationWriter;
import com.oneau.parser.ephemeris.Header;
import com.oneau.parser.ephemeris.Observation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    }

    public static void main(String[] args) {
    	System.setProperty(URL_PROPERTY, "jdbc:postgresql:oneau");
    	System.setProperty(USERNAME_PROPERTY,"postgres");
    	System.setProperty(PASSWORD_PROPERTY, "postgres");
    	System.setProperty(DRIVER_PROPERTY, "org.postgresql.Driver");
    	
    	JdbcObservationWriter w = new JdbcObservationWriter();
    	w.init();
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
            String dbtypekey = SqlGeneratorFactory.getDbTypeKey(
            		this.connection.getMetaData().getDatabaseProductName(),
            		this.connection.getMetaData().getDatabaseProductVersion()
            );
            
            logger.info("Using database type: "+dbtypekey);
            this.sqlGenerator = SqlGeneratorFactory.instance(dbtypekey);
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
        logger.finest("write() called");

        try {
            String headerInfo = sqlGenerator.writeHeader(header);
            if(null != headerInfo)
            	executeUpdate(headerInfo);

            String fileInfo = sqlGenerator.writeFileInfo(observation);
            if(null != fileInfo)
            	executeUpdate(fileInfo);

            String rangeInfo = sqlGenerator.writeRangeInfo(observation);
            if(null != rangeInfo)
            	executeUpdate(rangeInfo);

            for(String sql : sqlGenerator.writeObservations(observation))
            	executeUpdate(sql);

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void executeUpdate(String sql) throws SQLException {
        int result = connection.createStatement().executeUpdate(sql);
        if(result != 1) {
            logger.warning(format("insert returned [%d] when executing [%s]", result, sql));
        }

    }    
    
    private void executeStatement(String sql) throws SQLException {
    	Statement s = connection.createStatement();
        boolean result = s.execute(sql);
        if(!result) {
        	if(s.getUpdateCount() != 0)
        		logger.warning(format("DDL returned [%s (%d)] when executing [%s]", result,  s.getUpdateCount(), sql));
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
