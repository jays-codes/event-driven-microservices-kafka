# create a consumer
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning

# create a consumer group
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --group test-group --from-beginning

# create a consumer group
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --group test-group --from-beginning