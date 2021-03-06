# Kafka standalone server

This is a convenient application to run Kafka as a standalone server. It will start zookeeper and kafka orderly. It uses the "/tmp" directory to store working files. This Kafka standalone server is designed to simplify software development and testing and should not be used for production purpose.

Note that when you restart the Kafka standalone server, all topics will be deleted. This is intentional because the kafka standalone server is designed for dev and testing only.

## Limitation of the Kafka standalone server in Windows environment

The Kafka standalone server runs in Mac and Linux environments. When running in Windows environment, it will crash when the presence monitor tries to delete an expired topic.

If you really need to run the Kafka standalone server in a Windows laptop, please dockerize the kafka standalone application as follows.

## Using docker

If you are using Windows machine and you have "Docker for Windows" installed, the best way to run this kafka standalone server is to dockerize it.

```
docker build -t kafka-standalone .
docker run -p 9092:9092 -p 2181:2181 kafka-standalone
```

After this step, you can start/stop it from the Docker Desktop app.
