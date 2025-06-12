kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic-2 --property print.offset=true --group consumer-group-1

kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list