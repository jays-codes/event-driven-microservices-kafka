# event-driven-microservices-kafka
Jay's project/practice repo for Event-driven Microservice using Kafka

#### repo: event-driven-microservices-kafka
- updated readme

#### proj: kafka-commands
- modified consumer with timestamp option --property print.timestamp=true
- 04-print-offset.sh: consumer cmd with print.offset=true option
- set --timeout option on producer to send message given timeout (ms)
- file for commands for kafka-console-consumer.sh to consume messages from a specific topic
- file for commands for kafka-console-producer.sh to create messages on a specific topic
- file for commands related to kafka-topics.sh: create, delete, describe topic, list all topics
- new proj folder for kafka commands

#### proj: kafka-setup
- updated docker-compose (kafka-bitnami)
- created docker-compose files to use instructor custom kafka image, and bitnami image; updated gitignore to avoid including volume files