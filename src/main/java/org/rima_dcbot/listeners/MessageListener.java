package org.rima_dcbot.listeners;

import java.util.*;

import javax.annotation.Nonnull;

import org.rima_dcbot.crud.OptionsRepository;
import org.rima_dcbot.loader.JsonLoader;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.rima_dcbot.model.Options;
import org.rima_dcbot.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class MessageListener extends ListenerAdapter {
	private OptionsRepository optionsRepo;
	private JsonLoader loader;
	private Random rand;
	private double defaultWeight;

	public MessageListener(JsonLoader loader, OptionsRepository optionsRepo) {
		super();
		this.loader = loader;
		this.optionsRepo = optionsRepo;
		rand = new Random();
		defaultWeight = Utils.getDefaultWeight();
	}
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(!event.getAuthor().isBot()) {
			try {
				Optional<Options> userOptions = optionsRepo.findById(event.getAuthor().getId());
				double roll = rand.nextDouble();
				if (userOptions.isPresent() && !userOptions.get().getIsIgnored()) {
					double weight = userOptions.get().getChanceWeight();
					if (weight != 1.0 && roll > weight) return;
				}
				else if (userOptions.isPresent() && userOptions.get().getIsIgnored()) return;
				else if (roll > defaultWeight) return;
				
				Message msg = event.getMessage();
				Map<String, List<String>> json = loader.loadWordplays();

				String text = Utils.normalizeText(msg.getContentStripped().toLowerCase(Locale.ROOT));

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
			} catch (Exception e) {
				System.out.println("Error reading file: " + e);
				throw new RuntimeException(e);
			}
		}
	}
}
