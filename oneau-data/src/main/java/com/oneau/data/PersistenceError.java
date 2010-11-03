package com.oneau.data;

import java.sql.SQLException;

/**
 * Date: Aug 17, 2010
 * Time: 4:13:51 PM
 */
public class PersistenceError extends Error {
	private static final long serialVersionUID = -8523500546479422473L;
	
	private SQLException sqlException;
    public PersistenceError(SQLException e) {
        this.sqlException = e;
        this.initCause(e);
        this.setStackTrace(e.getStackTrace());
    }

    @Override
    public String getMessage() {
        return this.sqlException.getMessage();
    }
}
