package org.rima_dcbot.listeners;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import org.rima_dcbot.crud.OptionsRepository;
import org.rima_dcbot.loader.JsonLoader;
import org.rima_dcbot.model.Options;
import org.rima_dcbot.utils.Utils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageListener extends ListenerAdapter {
	private OptionsRepository optionsRepo;
	private JsonLoader loader;
	private Random rand;
	private double defaultWeight;

	private ArrayList<String> ignoredByTimeout;
	private TaskScheduler scheduler;

	public MessageListener(JsonLoader loader, OptionsRepository optionsRepo) {
		super();
		this.loader = loader;
		this.optionsRepo = optionsRepo;
		rand = new Random();
		defaultWeight = Utils.getDefaultWeight();
		ignoredByTimeout = new ArrayList<String>();
		scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
	}
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(!event.getAuthor().isBot()) {
			try {
				Optional<Options> userOptions = optionsRepo.findById(event.getAuthor().getId());
				double roll = rand.nextDouble();

				if (ignoredByTimeout.contains(event.getAuthor().getId())) return;
				else if (userOptions.isPresent() && !userOptions.get().getIsIgnored()) {
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

					if (userOptions.isPresent()
						&& userOptions.get().getTimeout() > 0
						&& !ignoredByTimeout.contains(userOptions.get().getDiscordId())
					) {
						activateUserTimeout(userOptions.get().getDiscordId(), userOptions.get().getTimeout());
					}
				}
			} catch (Exception e) {
				System.out.println("Error reading file: " + e);
				throw new RuntimeException(e);
			}
		}
	}

	@Async
	public void activateUserTimeout(String discordId, int timeout){
		ignoredByTimeout.add(discordId);

		scheduler.schedule(
			() -> ignoredByTimeout.remove(discordId),
			Instant.now().plusMillis(timeout * 1000L)
		);
	}
}
