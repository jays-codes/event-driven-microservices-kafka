kafka-topics.sh --bootstrap-server localhost:9092 --topic transfer-requests --create

kafka-topics.sh --bootstrap-server localhost:9092 --topic transaction-events --create

kafka-console-producer.sh --bootstrap-server localhost:9092 --topic transfer-requests --property parse.key=true --property key.separator=:

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic transaction-events --property print.offset=true --property print.key=true --isolation-level=read_committed --from-beginning

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic transaction-events --property print.offset=true --property print.key=true --from-beginning