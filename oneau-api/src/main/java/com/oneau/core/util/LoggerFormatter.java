package com.oneau.core.util;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {
	private static final String LOG_MESSAGE = "[%s] [%s] [%s] %s\n";
	@Override
	public String format(LogRecord record) {
		Date date = new Date(record.getMillis());
		return String.format(LOG_MESSAGE, date.toString(), record.getClass().getSimpleName(), record.getLevel().getName(), record.getMessage());
	}

}
