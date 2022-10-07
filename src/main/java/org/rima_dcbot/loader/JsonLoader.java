package org.rima_dcbot.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rima_dcbot.bean.DiscordUser;
import org.rima_dcbot.configuration.ConfigurationUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * JSON file loader
 *
 * @author jose
 *
 */
@Service
public class JsonLoader {
	private ObjectMapper om;
	private ObjectWriter ow;
	
	private File wordplaysFile;
	private Map<String, List<String>> wordplays;

	public JsonLoader() {
		om = new ObjectMapper();
		ow = om.writer(new DefaultPrettyPrinter() {
			private static final long serialVersionUID = 5673646862786127544L;
			
			@Override
			public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
				jg.writeRaw(": ");
			}
			@Override
			public DefaultPrettyPrinter createInstance() {
				return new DefaultPrettyPrinter(this);
			}
		});
		
		ConfigurationUtil config = ConfigurationUtil.getInstance();
		wordplaysFile = Paths.get(config.getProperty("JSON_PATH")).toFile();
	}
	
	public Map<String, List<String>> loadWordplays() throws IOException {
		if (wordplays == null)
			wordplays = om.readValue(wordplaysFile, Map.class);
		
		// return a copy to avoid unwanted modification of wordplays map
		// no need to deep copy, because key and values are strings
		return new HashMap<>(wordplays);
	}
	
	public void addWordplay(String suffix, String wordplay) throws IOException {
		// Use loadWordplays instead of direct reference to wordplaysFile
		// in case this is called before loadWordplays is ever called
		Map<String, List<String>> wordplays = loadWordplays();
		wordplays.compute(suffix, (k, v) -> {
			if(v == null) v = new ArrayList<String>();
			v.add(wordplay);
			return v;
		});
		ow.writeValue(wordplaysFile, wordplays);
		this.wordplays = wordplays;
	}
	
	public void removeWordplay(String suffix) throws IOException {
		Map<String, List<String>> wordplays = loadWordplays();
		wordplays.remove(suffix);
		ow.writeValue(wordplaysFile, wordplays);
		this.wordplays = wordplays;
	}
}
