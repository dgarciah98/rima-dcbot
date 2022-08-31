default: all

all: app build remove run

app:
	./gradlew jar

remove:
	docker stop rima-bot
	docker rm rima-bot

build:
	docker build --no-cache -f Dockerfile -t rima-bot .

run:
	docker run --restart on-failure --name rima-bot -dit -v ~/rima-dcbot/data:/rima-dcbot/data rima-bot
