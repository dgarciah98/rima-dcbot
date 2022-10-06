package org.rima_dcbot.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Optional;
import java.util.logging.Logger;

import org.rima_dcbot.bean.DiscordUser;
import org.rima_dcbot.configuration.ConfigurationUtil;
import org.rima_dcbot.crud.OptionsRepository;
import org.rima_dcbot.loader.JsonLoader;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.rima_dcbot.model.Options;
import org.rima_dcbot.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandListener extends ListenerAdapter {
	private OptionsRepository optionsRepo;
	private JsonLoader loader;
	private Logger changelogLogger;
	private Logger log;
	private File changelogFile;
	
	public SlashCommandListener(JsonLoader loader, OptionsRepository optionsRepo) {
		super();
		this.loader = loader;
		this.optionsRepo = optionsRepo;
		ConfigurationUtil config = ConfigurationUtil.getInstance();
		changelogFile = Paths.get(config.getProperty("CHANGELOG_PATH")).toFile();
		changelogLogger = config.getChangelogLogger();
		log = config.getStandardLogger();
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		switch (event.getName()) {
			case "stop":
				if (event.getJDA().getRegisteredListeners().stream().anyMatch(obj -> obj instanceof MessageListener)) {
					event.getJDA().getRegisteredListeners().forEach(obj -> {
						if (obj instanceof MessageListener)
							event.getJDA().removeEventListener(obj);
					});
					event.reply("Ya me porto bien").queue(ev ->
						ev.getJDA().getPresence().setActivity(Activity.listening("\uD83D\uDE07"))
					);
				}
				else event.reply("Pero si ahora no he hecho nada!!!!").queue();
				break;
		
			case "start":
				if (!event.getJDA().getRegisteredListeners().stream().anyMatch(obj -> obj instanceof MessageListener)) {
						event.getJDA().addEventListener(new MessageListener(this.loader, this.optionsRepo));
						event.reply("Holaaaa :)").queue(ev ->
							ev.getJDA().getPresence().setActivity(Activity.listening("\uD83D\uDE08"))
						);
					}
				else event.reply("Si ya estoy activao").queue();
				break;
			
			case "list":
				try {
					final String[] list = {""};
					loader.loadWordplays().forEach((k, v) -> {
						list[0] += k + " -> " + v.toString() + "\n";
					});
					event.replyFile(list[0].getBytes(StandardCharsets.UTF_8), "list.txt")
						.setEphemeral(true).queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "new":
				try {
					String suffix = event.getOption("suffix").getAsString();
					suffix = Utils.normalizeText(suffix);
					String wordplay = event.getOption("wordplay").getAsString();
					loader.addWordplay(suffix, wordplay);
					if (loader.loadWordplays().containsKey(suffix)) {
						event.reply("Rima añadida").queue();
						String record = event.getUser().getAsTag() + " added wordplay \"" + wordplay + "\" for suffix \"" + suffix + "\"";
						changelogLogger.info(record);
						log.info(record);
					} else event.reply("No se ha podido añadir la rima").queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "remove":
				try {
					String suffix = event.getOption("suffix").getAsString();
					suffix = Utils.normalizeText(suffix);
					loader.removeWordplay(suffix);
					if (!loader.loadWordplays().containsKey(suffix)) {
						event.reply("Rima eliminada").queue();
						String record = event.getUser().getAsTag() + " removed wordplays for suffix \"" + suffix + "\"";
						changelogLogger.info(record);
						log.info(record);
					} else event.reply("No se ha podido eliminar la rima").queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "ignoreme":
				try {
					Optional<Options> userOptions = optionsRepo.findById(event.getUser().getId());
					if(userOptions.isEmpty()) {
						Options newOptions = new Options(
							event.getUser().getId(),
							event.getUser().getName(),
							event.getUser().getDiscriminator(),
							Utils.getDefaultWeight()
						);
						optionsRepo.save(newOptions);
						userOptions = Optional.of(newOptions);
					}
					if(userOptions.get().getIsIgnored()) {
						userOptions.get().setIsIgnored(false);
						optionsRepo.save(userOptions.get());
						event.reply("Ya no te estoy ignorando").setEphemeral(true).queue();
					} else {
						userOptions.get().setIsIgnored(true);
						optionsRepo.save(userOptions.get());
						event.reply("A partir de ahora te ignoro").setEphemeral(true).queue();
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
			
			case "mychance":
				try {
					Optional<Options> userOptions = optionsRepo.findById(event.getUser().getId());
					if(userOptions.isEmpty()) {
						Options newOptions = new Options(
							event.getUser().getId(),
							event.getUser().getName(),
							event.getUser().getDiscriminator(),
							Utils.getDefaultWeight()
						);
						optionsRepo.save(newOptions);
						userOptions = Optional.of(newOptions);
					}
					int percentage = event.getOption("percentage").getAsInt();
					double weight = percentage / 100.0f;
					
					userOptions.get().setChanceWeight(weight);
					optionsRepo.save(userOptions.get());
					event.reply("Te responderé el " + percentage + "% de las veces").setEphemeral(true).queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
			
			case "forgetmychance":
				try {
					int defaultPercentage = (int) Utils.getDefaultWeight()* 100;
					
					Optional<Options> userOptions = optionsRepo.findById(event.getUser().getId());
					if(userOptions.isEmpty()) {
						Options newOptions = new Options(
							event.getUser().getId(),
							event.getUser().getName(),
							event.getUser().getDiscriminator(),
							defaultPercentage/100.0
						);
						optionsRepo.save(newOptions);
						userOptions = Optional.of(newOptions);
					}
					else userOptions.get().setChanceWeight(defaultPercentage/100.0);
					optionsRepo.save(userOptions.get());
					event.reply("Ya no tienes una probabilidad registrada. "
							+ "Te responderé el " + defaultPercentage + "% de las veces (valor por defecto)")
					.setEphemeral(true).queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
			
			case "changelog":
				event.replyFile(changelogFile).setEphemeral(true).queue();
				break;
				
			case "help": 
				event.reply("""
					rima_bot Commands:
					`/help` - Shows this message
					`/start` - Makes the bot listen to messages again if it was stopped
					`/stop` - Stops the bot from listening to messages
					`/list` - Lists all the available suffixes and wordplays
					`/new <suffix> <wordplay>` - Adds a new wordplay using a suffix
					`/remove <suffix>` - Removes an existing wordplay using a suffix
					`/ignoreme` - Toggles between the bot responding or not responding to you
					`/mychance <percentage>` - Add a percentage chance of the bot responding to you (e.g. 10, 50, 75 ...)
					`/forgetmychance` - Remove your custom chance for getting bot responses
					`/changelog` - See the changelog
					""").setEphemeral(true).queue();
				break;
		}
	}
}
