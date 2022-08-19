package org.rima_dcbot.configuration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigurationUtil {
	private static ConfigurationUtil instance;
	private Dotenv dotenv;
	private Logger changelogLogger;
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
					changelogFileHandler.setFormatter(new ChangelogFormatter());
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
}