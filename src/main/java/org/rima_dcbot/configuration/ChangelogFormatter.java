package org.rima_dcbot.configuration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogRecord;

public class ChangelogFormatter extends AbstractLoggerFormatter {
	
	public ChangelogFormatter() {
		super();
	}

	public ChangelogFormatter(ZoneId zoneId) {
		super(zoneId);
	}
	
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
        sb.append(Instant.ofEpochMilli(record.getMillis()).atZone(zoneId).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss, OOOO")));
		sb.append(": ");
		sb.append(record.getMessage());
		sb.append("\n");
		
		return sb.toString();
	}

}
