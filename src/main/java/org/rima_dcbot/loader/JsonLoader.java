package org.rima_dcbot.loader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.rima_dcbot.configuration.ConfigurationUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * JSON file loader
 * @author jose
 *
 */
public class JsonLoader {
	
	private ObjectMapper om;
	private ObjectWriter ow;
	private ConfigurationUtil configUtil;
	
	public JsonLoader() {
		om = new ObjectMapper();
		ow = om.writer(new DefaultPrettyPrinter() {
		    @Override
		    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
		        jg.writeRaw(": ");
		    }
		});
		configUtil = ConfigurationUtil.getInstance();
	}
	
	public Map<String, String> loadWordplays() throws IOException {
		Map<String, String> wordplays = null;
		
		try {
			wordplays = om.readValue(new File(configUtil.getProperty("files.wordplays.path")), Map.class);
		} catch (IOException e) {
			System.out.println("Error reading file: " + e);
		}
		
		return wordplays;
	}
	
	public void saveWordplays(Map<String, String> wordplays) throws IOException {
		ow.writeValue(new File(configUtil.getProperty("files.wordplays.path")), wordplays);
	}
	
	public void addWordplay(String suffix, String wordplay) throws IOException {
		// TODO find a better way to do this
		Map<String, String> wordplays = loadWordplays();
		wordplays.put(suffix, wordplay);
		saveWordplays(wordplays);
	}
	
}
