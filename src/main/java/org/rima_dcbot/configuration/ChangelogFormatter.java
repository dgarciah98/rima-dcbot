package org.rima_dcbot.configuration;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ChangelogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
		
        sb.append(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		sb.append(": ");
		sb.append(record.getMessage());
		sb.append("\n");
		
		return sb.toString();
	}

}
