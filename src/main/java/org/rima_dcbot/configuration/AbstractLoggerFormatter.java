package org.rima_dcbot.configuration;

import java.time.ZoneId;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public abstract class AbstractLoggerFormatter extends Formatter {
	protected ZoneId zoneId;
	
	protected AbstractLoggerFormatter() {
		zoneId = ZoneId.systemDefault();
	}

	protected AbstractLoggerFormatter(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	public abstract String format(LogRecord record);
}
