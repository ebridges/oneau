package com.oneau.data;

import org.junit.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: ebridges
 * Date: Nov 15, 2010
 */
public class DAOFactoryTest {

    @Test
    public void testGetInstance() throws SQLException {
        DAOFactory factory = DAOFactory.instance(
            DAOFactory.JDBC_URL,
            DAOFactory.JDBC_USERNAME,
            DAOFactory.JDBC_PASSWORD,
            DAOFactory.JDBC_DRIVER
        );
        EphemerisDAO dao = factory.getEphemerisDAO();
        assertNotNull(dao);
        assertTrue(dao instanceof EphemerisDAOImpl);
        DataSource dataSource = ((EphemerisDAOImpl)dao).getDataSource();
        assertNotNull(dataSource);
        Connection c = dataSource.getConnection();
        assertNotNull(c);
        assertTrue(c.isReadOnly());
        assertFalse(c.isClosed());
        assertTrue(c.getAutoCommit());
    }
}
