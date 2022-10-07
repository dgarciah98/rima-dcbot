package org.rima_dcbot.configuration;

import java.io.IOException;
import java.time.ZoneId;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigurationUtil {
	private static ConfigurationUtil instance;
	private Dotenv dotenv;
	private Logger changelogLogger;
	private Logger standardLogger;
	private FileHandler changelogFileHandler;

	private ConfigurationUtil() {
		dotenv = Dotenv.load();
	}

	public static ConfigurationUtil getInstance() {
		return instance == null ? instance = new ConfigurationUtil() : instance;
	}

	public String getProperty(String propertyName) {
		return dotenv.get(propertyName);
	}
	
	public Logger getChangelogLogger() {
		if (changelogLogger == null) {
			String changelogPath = dotenv.get("CHANGELOG_PATH");

			changelogLogger = Logger.getLogger("changelog");
			if (changelogPath != null) {
				try {
					changelogFileHandler = new FileHandler(changelogPath, true);
					changelogFileHandler.setFormatter(new ChangelogFormatter(getLoggerTimezoneId()));
					changelogLogger.addHandler(changelogFileHandler);
					changelogLogger.setUseParentHandlers(false);
				} catch (SecurityException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		
		return changelogLogger;
	}
	
	public Logger getStandardLogger() {
		if (standardLogger == null) {
			standardLogger = Logger.getLogger("standard-logger");
			ConsoleHandler handler = new ConsoleHandler();
			handler.setFormatter(new StandardLoggerFormatter(getLoggerTimezoneId()));
			standardLogger.addHandler(handler);
			standardLogger.setUseParentHandlers(false);
		}
		
		return standardLogger;
	}
	
	private ZoneId getLoggerTimezoneId() {
		String timezone = getProperty("LOGGER_TIMEZONE");
		if (timezone != null) return ZoneId.of(timezone);
		return ZoneId.of("UTC");
	}
}