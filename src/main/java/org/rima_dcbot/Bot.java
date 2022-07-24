package org.rima_dcbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.rima_dcbot.listeners.MessageListener;
import org.rima_dcbot.listeners.ReadyListener;
import org.rima_dcbot.listeners.SlashCommandListener;
import org.rima_dcbot.loader.JsonLoader;

import javax.security.auth.login.LoginException;

public class Bot {
	public static void main(String[] args) throws LoginException, InterruptedException {
		Dotenv dotenv = Dotenv.load();
		JsonLoader loader = new JsonLoader();
		JDA bot = JDABuilder.createLight(
				dotenv.get("BOT_TOKEN"),
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT
			)
			//.addEventListeners(new MessageListener(loader))   // will not listen to messages at first
			.addEventListeners(new ReadyListener())
			.addEventListeners(new SlashCommandListener(loader))
			.setActivity(Activity.listening("Lil B - 05 Fuck Em"))
			.build();
		
		bot.upsertCommand("help", "Shows info about this bot's slash commands").queue();
		bot.upsertCommand("start", "Makes the bot listen to messages again if it was stopped").queue();
		bot.upsertCommand("stop", "Stops the bot from listening to messages").queue();
		bot.upsertCommand("list", "Lists all the available suffixes and wordplays").queue();
		bot.upsertCommand("new", "Adds a new wordplay using a suffix")
			.addOption(OptionType.STRING, "suffix", "Word suffix", true)
			.addOption(OptionType.STRING, "wordplay", "Wordplay that rhymes with the suffix", true)
			.queue();
		bot.upsertCommand("remove", "Removes an existing wordplay using a suffix")
			.addOption(OptionType.STRING, "suffix", "Word suffix", true)
			.queue();
		bot.awaitReady();
	}
}