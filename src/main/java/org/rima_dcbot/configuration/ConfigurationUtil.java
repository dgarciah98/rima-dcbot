package org.rima_dcbot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationUtil {

	private static ConfigurationUtil instance;
	private Properties properties;
	
	private ConfigurationUtil() {
		properties = new Properties();
		
		InputStream file = getClass().getClassLoader().getResourceAsStream("config.properties");

		if (file != null) {
			try {
				properties.load(file);
			} catch (IOException e) {
				System.out.println("Error loading properties: " + e);
			}
		}
	}
	
	public static ConfigurationUtil getInstance() {
		if (instance == null) {
			instance = new ConfigurationUtil();
		}
		
		return instance;
	}
	
	
	public String getProperty(String propertyName) throws IOException {
		return properties.getProperty(propertyName);
	}
}