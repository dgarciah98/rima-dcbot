package org.rima_dcbot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.rima_dcbot.loader.JsonLoader;

import java.io.IOException;
import java.util.Locale;

public class SlashCommandListener extends ListenerAdapter {

    private JsonLoader loader;

    public SlashCommandListener(JsonLoader loader) {
        super();
        this.loader = loader;
    }

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
                event.getJDA().addEventListener(new MessageListener(loader));
                event.reply("Holaaaa :)").queue();
            }
            else event.reply("Si ya estoy activao").queue();
        }

        if (event.getName().equals("new")) {
            try {
                String suffix = event.getOption("suffix").getAsString();
                String wordplay = event.getOption("wordplay").getAsString();
                loader.addWordplay(suffix, wordplay);
                if(loader.loadWordplays().containsKey(suffix))
                    event.reply("Rima añadida").queue();
                else event.reply("No se ha podido añadir la rima").queue();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (event.getName().equals("remove")) {
            try {
                String suffix = event.getOption("suffix").getAsString();
                loader.removeWordplay(suffix);
                if(!loader.loadWordplays().containsKey(suffix))
                    event.reply("Rima eliminada").queue();
                else event.reply("No se ha podido eliminar la rima").queue();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (event.getName().equals("help")) {
            event.reply("""
                    rima_bot Commands:
                    `/help` - Shows this message
                    `/start` - Makes the bot listen to messages again if it was stopped
                    `/stop` - Stops the bot from listening to messages
                    `/new <suffix> <wordplay>` - Adds a new wordplay using a suffix
                    `/remove <suffix>` - Removes an existing wordplay using a suffix
                    """).queue();
        }
    }
}
