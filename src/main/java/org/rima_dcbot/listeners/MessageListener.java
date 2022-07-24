package org.rima_dcbot.listeners;

import java.io.IOException;
import java.text.Collator;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

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
				Map<String, ArrayList<String>> json = loader.loadWordplays();
				String text = msg.getContentStripped().toLowerCase(Locale.ROOT);
				text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}","");
				String word = text.substring(text.lastIndexOf(" ") + 1);
				Optional<String> result = json.keySet().stream().filter(key -> {
					if (msg.getContentStripped().equals(key)) return true;
					else if (word.equals(key)) return true;
					else return word.endsWith(key);
				}).findFirst();
				result.ifPresent(key -> {
					ArrayList<String> res = json.get(key);
					event.getChannel().sendMessage(res.get(new Random().nextInt(res.size()))).queue();
				});
			} catch (IOException e) {
				System.out.println("Error reading file: " + e);
				throw new RuntimeException(e);
			}
		}
	}
}
