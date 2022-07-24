package org.rima_dcbot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.rima_dcbot.bean.BlacklistedUser;
import org.rima_dcbot.loader.JsonLoader;

import java.io.IOException;
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
					event.reply("Ya me porto bien").queue();
				}
				else event.reply("Pero si ahora no he hecho nada!!!!").queue();
				break;
		
			case "start":
				if (!event.getJDA().getRegisteredListeners().stream().anyMatch(obj -> obj instanceof MessageListener)) {
						event.getJDA().addEventListener(new MessageListener(loader));
						event.reply("Holaaaa :)").queue();
					}
				else event.reply("Si ya estoy activao").queue();
				break;
			
			case "list":
				try {
					final String[] list = {""};
					loader.loadWordplays().forEach((k, v) -> {
						list[0] += k + " -> " + v.toString() + "\n";
					});
					event.reply(list[0]).setEphemeral(true).queue();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				break;
				
			case "new":
				try {
					String suffix = event.getOption("suffix").getAsString();
					suffix = Normalizer.normalize(suffix, Normalizer.Form.NFD).replaceAll("\\p{M}","");
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
					suffix = Normalizer.normalize(suffix, Normalizer.Form.NFD).replaceAll("\\p{M}","");
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
