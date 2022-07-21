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
	
	private ObjectMapper om;
	private ConfigurationUtil configUtil;
	
	public JsonLoader() {
		om = new ObjectMapper();
		configUtil = ConfigurationUtil.getInstance();
	}
	
	public Map<String, String> loadRhymes() throws IOException {
		Map<String, String> rhymes = null;
		
		try {
			rhymes = om.readValue(new File(configUtil.getProperty("files.rhymes.path")), Map.class);
		} catch (IOException e) {
			System.out.println("Error reading file: " + e);
		}
		
		return rhymes;
	}
}
