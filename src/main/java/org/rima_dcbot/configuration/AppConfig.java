package org.rima_dcbot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class AppConfig {
	
	@Bean
	public Dotenv dotenv() {
		return Dotenv.load();
	}
	
}
