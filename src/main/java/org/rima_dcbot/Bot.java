package org.rima_dcbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.rima_dcbot.listeners.MessageListener;
import org.rima_dcbot.listeners.ReadyListener;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException, InterruptedException {
        Dotenv dotenv = Dotenv.load();
        JDA bot = JDABuilder.createLight(
                        dotenv.get("BOT_TOKEN"),
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(new MessageListener())
                .addEventListeners(new ReadyListener())
                .setActivity(Activity.listening("Lil B - 05 Fuck Em"))
                .build();
        bot.awaitReady();
    }





}