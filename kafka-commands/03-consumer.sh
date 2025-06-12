# create a consumer
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning

# create a consumer group
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --group test-group --from-beginning

# create a consumer group
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --group test-group --from-beginning

# 04-print-offset.sh: consumer cmd with print.offset=true option
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic-2 --property print.offset=true \
--property print.timestamp=true