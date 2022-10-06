package org.rima_dcbot;

import org.rima_dcbot.loader.JsonLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@SpringBootApplication
@Import(Bot.class)
public class RimaDcbotApplication {

  public static void main(String[] args) {
     new SpringApplicationBuilder(RimaDcbotApplication.class).web(WebApplicationType.NONE).run(args);
  }
}
