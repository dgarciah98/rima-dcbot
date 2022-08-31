package org.rima_dcbot.configuration;

import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class StandardLoggerFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		sb.append(record.getLevel());
		sb.append(" ");
        sb.append(Instant.ofEpochMilli(record.getMillis()));
		sb.append("] ");
		sb.append(record.getMessage());
		sb.append("\n");
		
		return sb.toString();
	}

}
