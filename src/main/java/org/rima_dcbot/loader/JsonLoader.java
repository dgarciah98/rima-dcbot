package org.rima_dcbot.loader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.rima_dcbot.configuration.ConfigurationUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON file loader
 * @author jose
 *
 */
public class JsonLoader {
	
	private static ObjectMapper om;
	private static ConfigurationUtil configUtil;
	
	public JsonLoader() {
		om = new ObjectMapper();
		configUtil = ConfigurationUtil.getInstance();
	}
	
	public static Map<String, String> loadWordplays() throws IOException {
		Map<String, String> wordplays = null;
		
		try {
			wordplays = om.readValue(new File(configUtil.getProperty("files.wordplays.path")), Map.class);
		} catch (IOException e) {
			System.out.println("Error reading file: " + e);
		}
		
		return wordplays;
	}
}
