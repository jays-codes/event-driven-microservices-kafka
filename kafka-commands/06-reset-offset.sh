#reset an offest for a particular topic for a particular 
#consumer group (no commit --dryrun) shifting by -5
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group-4 --topic test-topic-3 --reset-offsets --shift-by -5 --dry-run

#reset an offest for a particular topic for a particular 
#consumer group (no commit --dryrun) to-earliest
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group-4 --topic test-topic-3 --reset-offsets --to-earliest --dry-run

#reset an offest for a particular topic for a particular 
#consumer group (no commit --dryrun) to-latest
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group-4 --topic test-topic-3 --reset-offsets --to-latest --dry-run

#reset an offest for a particular topic for a particular 
#consumer group (no commit --dryrun) to-current
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-group-4 --topic test-topic-3 --reset-offsets --to-current --dry-run



