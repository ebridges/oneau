package com.oneau.core.util;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;

public class LoggerConsoleHandler extends ConsoleHandler {
    @Override
    protected void setOutputStream(OutputStream outputStream) throws SecurityException {
        super.setOutputStream(System.out);
    }

    @Override
    public void setFormatter(Formatter formatter) throws SecurityException {
        super.setFormatter(new LoggerFormatter());
    }
}
