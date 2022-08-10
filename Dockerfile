FROM openjdk:16-slim
WORKDIR /rima-dcbot
RUN apt-get update
RUN mkdir data
VOLUME ~/rima-dcbot/data
COPY .env /rima-dcbot/.
COPY build/libs/rima-dcbot.jar /rima-dcbot/.
CMD java -jar -Xms64m -Xmx64m rima-dcbot.jar
