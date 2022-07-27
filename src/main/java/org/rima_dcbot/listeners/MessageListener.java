package org.rima_dcbot.listeners;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

import javax.annotation.Nonnull;

import org.rima_dcbot.bean.BlacklistedUser;
import org.rima_dcbot.loader.JsonLoader;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	private JsonLoader loader;
	
	public MessageListener(JsonLoader loader) {
		super();
		this.loader = loader;
	}
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(!event.getAuthor().isBot()) {
			try {
				Message msg = event.getMessage();
				Map<String, List<String>> json = loader.loadWordplays();
				List<BlacklistedUser> blacklist = loader.loadBlacklist();
				if (!blacklist.contains(new BlacklistedUser(event.getAuthor()))) {
					
					String text = Normalizer.normalize(
							msg.getContentStripped().toLowerCase(Locale.ROOT)
								// replace ñ and ç to random chars to bypass them in the normalizer
								.replace('ñ', '\001')
								.replace('ç', '\002'),
							Normalizer.Form.NFD
						)
						.replaceAll("\\p{M}","")
						
						// replace ñ and ç back
						.replace('\001', 'ñ')
						.replace('\002', 'ç')
						// filter other marks like parenthesis, dots, exclamation, etc
						.replaceAll("[^\\p{IsLatin}\\d\\s]", "");
					
					String word = text.substring(text.lastIndexOf(" ") + 1);
					
					// ignore urls
					if(text.startsWith("http") || word.startsWith("http")) return;
					
					List<String> results = json.keySet().stream().filter(key ->
						text.equals(key) || word.equals(key) || word.endsWith(key)
					).toList();
					if(!results.isEmpty()) {
						String key = results.stream().findFirst().get();
						// filter might find words that end with the same suffix, but you want a specific suffix/word
						if (results.contains(text))
							key = results.get(results.indexOf(text));
						else if (results.contains(word))
							key = results.get(results.indexOf(word));
						List<String> res = json.get(key);
						event.getChannel().sendMessage(res.get(new Random().nextInt(res.size()))).queue();
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + e);
				throw new RuntimeException(e);
			}
		}
	}
}
