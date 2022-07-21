package org.rima_dcbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.rima_dcbot.loader.JsonLoader;

public class Bot {
    public static void main(String[] args) throws LoginException, IOException {
//        Dotenv dotenv = Dotenv.load();
//        JDA bot = JDABuilder.createDefault(dotenv.get("BOT_TOKEN"))
//                .setActivity(Activity.listening("Lil B - 05 Fuck Em"))
//                .build();
    	
    	JsonLoader loader = new JsonLoader();
    	System.out.println(loader.loadRhymes());
    }
}