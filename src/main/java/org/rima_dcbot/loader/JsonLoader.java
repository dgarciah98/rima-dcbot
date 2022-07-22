package org.rima_dcbot.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.rima_dcbot.configuration.ConfigurationUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * JSON file loader
 * 
 * @author jose
 *
 */
public class JsonLoader {

	private ObjectMapper om;
	private ObjectWriter ow;

	private File wordplaysFile;
	private Map<String, String> wordplays;

	public JsonLoader() {
		om = new ObjectMapper();
		ow = om.writer(new DefaultPrettyPrinter() {
			@Override
			public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
				jg.writeRaw(": ");
			}
		});

		wordplaysFile = Paths.get(ConfigurationUtil.getInstance().getProperty("files.wordplays.path")).toFile();
	}

	public Map<String, String> loadWordplays() throws IOException {
		if (wordplays == null) {
			wordplays = om.readValue(wordplaysFile, Map.class);
		}
		
		// return a copy to avoid unwanted modification of wordplays map
		// no need to deep copy, because key and values are strings
		return new HashMap<>(wordplays);
	}

	public void addWordplay(String suffix, String wordplay) throws IOException {
		// Use loadWordplays instead of direct reference to wordplaysFile in case this is called before loadWordplays is ever called
		loadWordplays().put(suffix, wordplay);
		ow.writeValue(wordplaysFile, wordplays);
	}

}
