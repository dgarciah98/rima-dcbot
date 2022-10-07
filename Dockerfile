FROM openjdk:16-slim
WORKDIR /rima-dcbot
RUN apt-get update
run apt-get install sqlite
RUN mkdir data
RUN mkdir db
VOLUME ~/rima-dcbot/data
COPY .env /rima-dcbot/.
COPY db/create-db.sh /rima-dcbot/db/.
RUN chmod a+rw /rima-dcbot/db/create-db.sh && \
    ./rima-dcbot/db/create-db.sh
COPY build/libs/rima-dcbot.jar /rima-dcbot/.
CMD java -jar -Xms64m -Xmx64m rima-dcbot.jar
