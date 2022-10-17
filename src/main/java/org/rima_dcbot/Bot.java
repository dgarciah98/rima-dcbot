package org.rima_dcbot;

import javax.security.auth.login.LoginException;

import org.rima_dcbot.crud.OptionsRepository;
import org.rima_dcbot.listeners.ReadyListener;
import org.rima_dcbot.listeners.SlashCommandListener;
import org.rima_dcbot.loader.JsonLoader;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bot implements CommandLineRunner {

  @Autowired	
  private OptionsRepository optionRepo;
  
  @Autowired
  private JsonLoader loader;
  
  @Autowired
  private Dotenv dotenv;
  
  @Override
  public void run(String... args) throws Exception {
    main(args);
  }
  
  public void main(String[] args) throws LoginException, InterruptedException {
    JDA bot = JDABuilder.createLight(
        dotenv.get("BOT_TOKEN"),
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT
      )
      //.addEventListeners(new MessageListener(loader))   // will not listen to messages at first
      .addEventListeners(new ReadyListener())
      .addEventListeners(new SlashCommandListener(loader, optionRepo))
      .setActivity(Activity.listening("\uD83D\uDE07"))
      .build();
    
    upsertBotCommands(bot);
    
    bot.awaitReady();
  }
  
  private void upsertBotCommands(JDA bot) {
    OptionData percentage = new OptionData(OptionType.INTEGER, "percentage", "Percentage as a number, without symbol, between 0-100", true);
    percentage.setRequiredRange(0L, 100L);
  
    bot.upsertCommand("help",
      "Shows info about this bot's slash commands").queue();
    bot.upsertCommand("start",
      "Makes the bot listen to messages again if it was stopped").queue();
    bot.upsertCommand("stop",
      "Stops the bot from listening to messages").queue();
    bot.upsertCommand("list",
      "Lists all the available suffixes and wordplays").queue();
    bot.upsertCommand("new",
        "Adds a new wordplay using a suffix")
      .addOption(OptionType.STRING, "suffix", "Word suffix", true)
      .addOption(OptionType.STRING, "wordplay", "Wordplay that rhymes with the suffix", true)
      .queue();
    bot.upsertCommand("remove",
        "Removes an existing wordplay using a suffix")
      .addOption(OptionType.STRING, "suffix", "Word suffix", true)
      .queue();
    bot.upsertCommand("ignoreme",
      "Toggles between the bot responding or not responding to you").queue();
    bot.upsertCommand("setchance",
        "Add a percentage chance of the bot responding to you (e.g. 10, 50, 75 ...)")
      .addOptions(percentage)
      .queue();
    bot.upsertCommand("forgetchance",
        "Remove your custom chance for getting bot responses")
      .queue();
    bot.upsertCommand("settimeout",
        "Add a time interval where the bot won't reply after its last response (in seconds)")
      .addOption(OptionType.INTEGER, "timeout", "Time in seconds", true)
      .queue();
    bot.upsertCommand("forgettimeout",
        "Remove your custom timeout")
      .queue();
    bot.upsertCommand("changelog",
        "See the changelog")
      .queue();
  }
}