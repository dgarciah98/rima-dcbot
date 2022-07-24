package org.rima_dcbot.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rima_dcbot.bean.BlacklistedUser;
import org.rima_dcbot.configuration.ConfigurationUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
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
	
	private File blacklistFile;
	private List<BlacklistedUser> blacklist;
	
	public JsonLoader() {
		om = new ObjectMapper();
		ow = om.writer(new DefaultPrettyPrinter() {
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
		blacklistFile = Paths.get(config.getProperty("BLACKLIST_PATH")).toFile();
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
		// Use loadWordplays instead of direct reference to wordplaysFile
		// in case this is called before loadWordplays is ever called
		Map<String, String> wordplays = loadWordplays();
		wordplays.put(suffix, wordplay);
		ow.writeValue(wordplaysFile, wordplays);
		this.wordplays = wordplays;
	}

	public void removeWordplay(String suffix) throws IOException {
		Map<String, String> wordplays = loadWordplays();
		wordplays.remove(suffix);
		ow.writeValue(wordplaysFile, wordplays);
		this.wordplays = wordplays;
	}
	
	public List<BlacklistedUser> loadBlacklist() throws IOException {
		if (blacklist == null) {
			blacklist = om.readValue(blacklistFile,  new TypeReference<List<BlacklistedUser>>(){});
		}
		
		return copyBlacklist();
	}
	
	public void blacklistUser(BlacklistedUser user) throws IOException {
		blacklistUser(user.getUsername(), user.getDiscriminator());
	}
	
	public void blacklistUser(String username, String discriminator) throws IOException {
		List<BlacklistedUser> list = loadBlacklist();
		list.add(new BlacklistedUser(username, discriminator));
		ow.writeValue(blacklistFile, list);
		blacklist = list;
	}
	
	public void whitelistUser(String username, String discriminator) throws IOException {
		whitelistUser(new BlacklistedUser(username, discriminator));
	}
	
	public void whitelistUser(BlacklistedUser user) throws IOException {
		List<BlacklistedUser> list = loadBlacklist();
		list.removeIf(u -> u.equals(user));
		ow.writeValue(blacklistFile, list);
		blacklist = list;
	}
	
	private List<BlacklistedUser> copyBlacklist() {
		List<BlacklistedUser> l = new ArrayList<>();
		
		for (BlacklistedUser u : blacklist) {
			l.add(u.copy());
		}
		
		return l;
	}
}
