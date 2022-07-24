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
							msg.getContentStripped().toLowerCase(Locale.ROOT),
							Normalizer.Form.NFD
						)
						.replaceAll("\\p{M}", "");
					String word = text.substring(text.lastIndexOf(" ") + 1);
					
					// ignore urls
					if(text.startsWith("http") || word.startsWith("http")) return;
					
					Optional<String> result = json.keySet().stream().filter(key ->
						/*if (text.equals(key)) return true;
						else if (word.equals(key)) return true;
						else if (word.endsWith(key)) return true;
						else return word.contains(key);*/
						text.equals(key) || word.equals(key) || word.endsWith(key) // || word.contains(key); // i.e. "cinco?".contains("inco") but may result in incorrect behaviour
					).findFirst();
					
					result.ifPresent(key -> {
						List<String> res = json.get(key);
						event.getChannel().sendMessage(res.get(new Random().nextInt(res.size()))).queue();
					});
				}
			} catch (IOException e) {
				System.out.println("Error reading file: " + e);
				throw new RuntimeException(e);
			}
		}
	}
}
