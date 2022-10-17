default: all

all: app docker-build docker-remove docker-run

app:
	./gradlew bootJar

docker-remove:
	docker stop rima-bot
	docker rm rima-bot

docker-build:
    sh ./db/create-db.sh
    if [ -f options.db ]; then mv options.db db/.; fi
	docker build --no-cache -f Dockerfile -t rima-bot .

docker-run:
	docker run --restart on-failure --name rima-bot -dit -v ~/rima-dcbot/data:/rima-dcbot/data -v ~/rima-dcbot/db:/rima-dcbot/db rima-bot
