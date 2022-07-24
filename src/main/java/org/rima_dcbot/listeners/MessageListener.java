package org.rima_dcbot.listeners;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            Message msg = event.getMessage();
            try {
                Map<String, String> json = loader.loadWordplays();
                List<BlacklistedUser> blacklist = loader.loadBlacklist();
                if (!blacklist.contains(new BlacklistedUser(event.getAuthor()))) {
	                String text = msg.getContentStripped().toLowerCase(Locale.ROOT);
	                String word = text.substring(text.lastIndexOf(" ") + 1);
	                json.keySet().forEach(key -> {
	                    if(word.endsWith(key))
	                        event.getChannel().sendMessage(json.get(key)).queue();
	                });
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e);
                throw new RuntimeException(e);
            }
        }
    }
}
