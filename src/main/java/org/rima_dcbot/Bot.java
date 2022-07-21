package org.rima_dcbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws LoginException {
        Dotenv dotenv = Dotenv.load();
        JDA bot = JDABuilder.createDefault(dotenv.get("BOT_TOKEN"))
                .setActivity(Activity.listening("Lil B - 05 Fuck Em"))
                .build();
    }
}