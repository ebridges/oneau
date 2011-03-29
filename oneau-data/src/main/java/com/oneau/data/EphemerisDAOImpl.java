package com.oneau.data;

import com.oneau.core.EphemerisDataFile;
import com.oneau.core.EphemerisDataView;
import com.oneau.core.EphemerisDataViewImpl;
import com.oneau.core.util.ChebyshevTime;
import com.oneau.core.util.HeavenlyBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Date: Aug 17, 2010
 * Time: 3:23:26 PM
 */
public class EphemerisDAOImpl implements EphemerisDAO {
    private static final Logger logger = Logger.getLogger(EphemerisDAOImpl.class.getName());
    private DataSource dataSource;
    private static final String COEFFICIENTS_BY_DATE_SQL = "SELECT COEFFICIENT FROM ONEAU.OBSERVATION LEFT JOIN ONEAU.EPHEMERIS_DATA D ON FILE_ID = D.ID WHERE INTERVAL_ID = ? AND MEASURED_ITEM_ID = ? AND D.FILENAME = ?";
    private static final String LOOKUP_INTERVAL_ID = "SELECT id AS interval_id FROM oneau.ephemeris_interval WHERE ? BETWEEN range_from AND range_to";

    public EphemerisDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public EphemerisDataView getCoefficientsByDate(EphemerisDataFile source, HeavenlyBody planet, Double julianDate) {
        logger.info(format("EphemerisDAOImpl#getCoefficientsByDate('%s', '%s')",  planet.getName(), julianDate.toString()));
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ChebyshevTime interpolatedTime = new ChebyshevTime(source.getBeginDate(), EphemerisDataFile.INTERVAL_DURATION);
        try {
            c = dataSource.getConnection();
            Integer intervalId = lookupIntervalId(c, julianDate);
            ps = c.prepareStatement(COEFFICIENTS_BY_DATE_SQL);
            ps.setInt(1, intervalId);
            ps.setInt(2, planet.getId());
            ps.setString(3, source.getFileName());
            rs = ps.executeQuery();

            List<Double> coefficients = new LinkedList<Double>();
            while(rs.next()) {
                coefficients.add(rs.getDouble("coefficient"));
            }

            return new EphemerisDataViewImpl(planet, julianDate, interpolatedTime, coefficients);
        } catch (SQLException e) {
            throw new PersistenceError(e);
        }finally {
            try {
                if(null != rs) {
                    rs.close();
                }
                if(null != ps) {
                    ps.close();
                }
                if(null != c) {
                    c.close();
                }
            } catch (SQLException e) {
                //noinspection ThrowFromFinallyBlock
                throw new PersistenceError(e);
            }
        }
    }

    private Integer lookupIntervalId(Connection c, Double julianDate) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = c.prepareStatement(LOOKUP_INTERVAL_ID);
            ps.setDouble(1, julianDate);
            rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("interval_id");
            } else {
                throw new EphemerisIntervalNotFound(julianDate);
            }
        } finally {
            if(null != rs) {
                rs.close();
            }
            if(null != ps) {
                ps.close();
            }
        }
    }

    DataSource getDataSource() {
        return dataSource;
    }
}
