# create a topic
kafka-topics.sh --bootstrap-server localhost:9092 --topic test-topic --create --partitions 1 --replication-factor 1

# list topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# describe topic
kafka-topics.sh --bootstrap-server localhost:9092 --topic test-topic --describe

# delete topic
kafka-topics.sh --bootstrap-server localhost:9092 --topic test-topic --delete

