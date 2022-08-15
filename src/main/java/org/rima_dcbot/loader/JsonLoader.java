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
	private Map<String, List<String>> wordplays;

	private File blacklistFile;
	private List<DiscordUser> blacklist;
	
	private File weightsFile;
	private Map<String, Float> weights;
	
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
		blacklistFile = Paths.get(config.getProperty("BLACKLIST_PATH")).toFile();
		weightsFile = Paths.get(config.getProperty("WEIGHTS_PATH")).toFile();
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

	public List<DiscordUser> loadBlacklist() throws IOException {
		if (blacklist == null) {
			blacklist = om.readValue(blacklistFile,  new TypeReference<List<DiscordUser>>(){});
		}
		
		return copyBlacklist();
	}
	
	public void blacklistUser(DiscordUser user) throws IOException {
		blacklistUser(user.getUsername(), user.getDiscriminator());
	}
	
	public void blacklistUser(String username, String discriminator) throws IOException {
		List<DiscordUser> list = loadBlacklist();
		list.add(new DiscordUser(username, discriminator));
		ow.writeValue(blacklistFile, list);
		blacklist = list;
	}
	
	public void whitelistUser(String username, String discriminator) throws IOException {
		whitelistUser(new DiscordUser(username, discriminator));
	}
	
	public void whitelistUser(DiscordUser user) throws IOException {
		List<DiscordUser> list = loadBlacklist();
		list.removeIf(u -> u.equals(user));
		ow.writeValue(blacklistFile, list);
		blacklist = list;
	}
	
	public Map<String, Float> loadWeights() throws IOException {
		if (weights == null)
			weights = om.readValue(weightsFile, Map.class);
		
		// no need to deep copy, because key and values are strings
		return new HashMap<>(weights);
	}
	
	public void addOrUpdateWeight(DiscordUser user, float weight) throws IOException {
		addOrUpdateWeight(user.toString(), weight);
	}
	
	public void addOrUpdateWeight(String user, float weight) throws IOException {
		Map<String, Float> weights = loadWeights();
		weights.put(user, weight);
		ow.writeValue(weightsFile, weights);
		this.weights = weights;
	}
	
	public void removeWeight(DiscordUser user) throws IOException {
		removeWeight(user.toString());
	}
	
	public void removeWeight(String user) throws IOException {
		Map<String, Float> weights = loadWeights();
		weights.remove(user);
		ow.writeValue(weightsFile, weights);
		this.weights = weights;
	}
	
	private List<DiscordUser> copyBlacklist() {
		List<DiscordUser> l = new ArrayList<>();
		
		for (DiscordUser u : blacklist) {
			l.add(u.copy());
		}
		
		return l;
	}
	
}
