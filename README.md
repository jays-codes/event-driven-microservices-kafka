# event-driven-microservices-kafka
Jay's project/practice repo for Event-driven Microservice using Kafka

#### repo: event-driven-microservices-kafka
- updated readme

#### proj: kafka-commands
- added cmd (consumer-groups.sh) --group --describe; demoed lag count before and after stopping and after restarting consumer (producing msgs between)
- test command to modify partition count in a topic
- demoed passing key:msg-val in a producer via --property key.separator=: and parse.key=true;
and have consumer print key using --property print.key=true; used new topic created earlier with 
2 partitions; 2 consumer in a consumer-group consuming
- specify # of partition to create for topic : 01-topic.sh --partitions
- kafka-consumer-groups.sh - for managing consumer groups: --list
- Consumer Group: added consumer cmd with --group option
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