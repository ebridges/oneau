package com.oneau.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {
	private static final String LOG_MESSAGE = "[%s] [%s] [%s] %s\n";
    private static final Map<Level, String> LEVELS = new HashMap<Level, String>();

    static {
        LEVELS.put(Level.FINE, "F");
        LEVELS.put(Level.FINER, "F2");
        LEVELS.put(Level.FINEST, "F3");
        LEVELS.put(Level.INFO, "I");
        LEVELS.put(Level.SEVERE, "E");
        LEVELS.put(Level.WARNING, "W");
    }

    private DateFormat format;

    public LoggerFormatter() {
        this.format = new SimpleDateFormat("MM/dd HH:mm:ss.SS");
    }

    @Override
	public String format(LogRecord record) {
		Date date = new Date(record.getMillis());
		return String.format(LOG_MESSAGE, format.format(date), record.getClass().getSimpleName(), LEVELS.get(record.getLevel()), record.getMessage());
	}

}
