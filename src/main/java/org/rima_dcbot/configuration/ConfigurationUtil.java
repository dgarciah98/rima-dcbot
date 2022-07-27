package org.rima_dcbot.configuration;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationUtil {
	private static ConfigurationUtil instance;
	private Dotenv dotenv;
	
	private ConfigurationUtil() {
		dotenv = Dotenv.load();
	}
	
	public static ConfigurationUtil getInstance(){
		return instance == null ? instance = new ConfigurationUtil() : instance;
	}
	
	public String getProperty(String propertyName) {
		return dotenv.get(propertyName);
	}
}