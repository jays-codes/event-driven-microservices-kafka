# create a producer
kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic

# create a producer with timeout
kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic --timeout 10

# produce messages
kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic --producer-property acks=all

# produce messages with key
kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic --property parse.key=true --property key.separator=:

kafka-console-producer.sh \
--bootstrap-server localhost:9092 \
--topic test-topic-3 \
--property key.separator=: \
--property parse.key=true \

