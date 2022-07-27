package org.rima_dcbot.listeners;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.rima_dcbot.bean.BlacklistedUser;
import org.rima_dcbot.loader.JsonLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

public class SlashCommandListener extends ListenerAdapter {
	private JsonLoader loader;
	
	public SlashCommandListener(JsonLoader loader) {
		super();
		this.loader = loader;
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
						event.getJDA().addEventListener(new MessageListener(loader));
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
					suffix = Normalizer.normalize(
						suffix
							// replace ñ and ç to random chars to bypass them in the normalizer
							.replace('ñ', '\001')
							.replace('ç', '\002'),
						Normalizer.Form.NFD)
						.replaceAll("\\p{M}","")
						// replace ñ and ç back
						.replace('\001', 'ñ')
						.replace('\002', 'ç');
					String wordplay = event.getOption("wordplay").getAsString();
					loader.addWordplay(suffix, wordplay);
					if (loader.loadWordplays().containsKey(suffix))
						event.reply("Rima añadida").queue();
					else event.reply("No se ha podido añadir la rima").queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "remove":
				try {
					String suffix = event.getOption("suffix").getAsString();
					suffix = Normalizer.normalize(
							suffix
								// replace ñ and ç to random chars to bypass them in the normalizer
								.replace('ñ', '\001')
								.replace('ç', '\002'),
							Normalizer.Form.NFD)
						.replaceAll("\\p{M}","")
						// replace ñ and ç back
						.replace('\001', 'ñ')
						.replace('\002', 'ç');
					loader.removeWordplay(suffix);
					if (!loader.loadWordplays().containsKey(suffix))
						event.reply("Rima eliminada").queue();
					else event.reply("No se ha podido eliminar la rima").queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "ignoreme":
				try {
					List<BlacklistedUser> blacklist = loader.loadBlacklist();
					BlacklistedUser author = new BlacklistedUser(event.getUser());
					if (blacklist.contains(author)) {
						loader.whitelistUser(author);
						event.reply("Ya no te estoy ignorando").setEphemeral(true).queue();
					} else {
						loader.blacklistUser(author);
						event.reply("A partir de ahora te ignoro").setEphemeral(true).queue();
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
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
					""").setEphemeral(true).queue();
				break;
		}
	}
}
