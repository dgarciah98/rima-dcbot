FROM openjdk:16-slim
WORKDIR /rima-dcbot
RUN apt-get update
RUN apt-get install -y sqlite3
RUN mkdir data
RUN mkdir db
VOLUME ~/rima-dcbot/data
VOLUME ~/rima-dcbot/db
COPY .env /rima-dcbot/.
RUN chmod +x /rima-dcbot/db/create-db.sh && \
    sh /rima-dcbot/db/create-db.sh && \
    mv /rima-dcbot/options.db /rima-dcbot/db/.
COPY build/libs/rima-dcbot.jar /rima-dcbot/.
CMD java -jar -Xms64m -Xmx64m rima-dcbot.jar
