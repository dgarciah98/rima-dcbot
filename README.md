# rima-dcbot
Stupid and annoying Discord bot that will make a dumb spanish wordplay after the last word you write. Made with JDA.

## Build and run

Before running, you will have to provide a bot token of your own in your .env. More details below.

### Gradle
You may run `gradlew` to build the JAR and then run the builded app to run the bot.


```bash
$ ./gradlew jar
$ java -jar -Xms64m -Xmx64m rima-dcbot.jar
```

You will need to provide your token in your .env, using the example.env provided. 

### Docker

You may use the Makefile (`make`) to build and run the Docker image.

You will also need to set up the .env.

## Environment

You will need to make yourself a bot application in the Discord Developer Portal.

```
 BOT_TOKEN="token"
 JSON_PATH="data/example.wordplays.json"
 CHANGELOG_PATH="example.changelog.txt"
```
