kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic-2 --property print.offset=true --group consumer-group-1

kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic-3 --property print.key=true --property print.offset=true --group consumer-group-1

#reset an offest for a particular topic for a particular consumer group (no commit --dryrun)
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group-4 --topic test-topic-3 --reset-offsets --shift-by -5 --dry-run