package org.rima_dcbot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("stop")) {
            if(event.getJDA().getRegisteredListeners().stream().anyMatch(obj -> obj instanceof MessageListener)) {
                event.getJDA().getRegisteredListeners().forEach(obj -> {
                    if (obj instanceof MessageListener) event.getJDA().removeEventListener(obj);
                });
                event.reply("Ya me porto bien").queue();
            }
            else event.reply("Pero si ahora no he hecho nada!!!!").queue();
        }

        if (event.getName().equals("start")) {
            if(!event.getJDA().getRegisteredListeners().stream().anyMatch(obj -> obj instanceof MessageListener)) {
                event.getJDA().addEventListener(new MessageListener());
                event.reply("Holaaaa :)").queue();
            }
            else event.reply("Si ya estoy activao").queue();
        }
    }
}
