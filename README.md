# event-driven-microservices-kafka
Jay's project/practice repo for Event-driven Microservice using Kafka


#### proj: reactive-kafka-sandbox (jayslabs.kafka; SpringBoot 3.5.0, jdk 21;Spring Reactive Web, Spring for Apache Kafka, lombok, reactor-kafka)
- section7.parallel.KafkaConsumer: UC:Parallel Processing using flatMap()
- section7.batch.KafkaConsumer: UC:Batch Processing; called receiveAutoAck(), (Flux) concatMap(KafkaConsumer::batchProcess), HOF batchProcess(Flux<ConsumerRecord>):Mono<Void>
- package:section7.batch - initial create of classes for Batch and Parallel Processing
- package:section4.seekoffset: KafkaProducer, KafkaConsumer: uses .addAssignListener(), Collection<ReceiverPartition>, ReceiverPartition.topicPartition(), .partition(), .position(), .seek();
seekToLastMessagesForPartition()  - to go to last N message of a given x partition; seekToBeginning(), seekToEnd(), seekToTimestamp() 
- [BP] set ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG : CooperativeStickyAssignor - for partition reassignment to consumer instances in cg
- section4.consumergroup.KafkaConsumer/KafkaProducer/KafkaConsumerGroup; setup topic (order-events) with 3 partitions
- KafkaProducer: add message headers to ProducerRecord. Used RecordHeaders
- [BP] Produce and Consume 1,000,000 Events; package: kafka.section4.onemillionevents: KafkaProducer, KafkaConsumer
- KafkaProducer: sender::close on doOnComplete
- KafkaProducer: created Flux of events (SenderRecord(prec, prec.key()), ProducerRecord - prec) to send to order-events topic;
called .send(flex), and .doOnNext(SenderResult), logging in correlation id from senderResult
- created KafkaProducer class:ProducerConfig, SenderOptions.create(config), KafkaSender.create(options)
- MultipleTopicsConsumer: class to demo consuming from multiple topics; used regex for topic names
- KafkaConsumer: set ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true for automatic message acknowledgement
- KafkaConsumer: called record.receiverOffset().acknowledge() on doOnNext() on .receive() to acknowledge consumer receiving message
- KafkaConsumer: set ConsumerConfig.GROUP_INSTANCE_ID_CONFIG to speed up partition reassignment after a restart
- KafkaConsumer: set ConsumerConfig.AUTO_OFFSET_REST_CONFIG to earliest to get all events in topic for consumer group
- KafkaConsumer: defined ConsumerConfig(config), defined ReceiverOptions (options) using config: .create(), .subscription(<topics>), create instance of KafkaReceiver class passing in options: create(), receive():Flux<ReceiverRecord>
- created KafkaConsumer demo class; ConsumerConfig,  properties: BOOTSTRAP_SERVERS_CONFIG, KEY_DESERIALIZER_CLASS_CONFIG, VALUE_DESERIALIZER_CLASS_CONFIG, GROUP_ID_CONFIG
- initial proj commit incl dep: reactive-kafka-sandbox (jayslabs.kafka; SpringBoot 3.5.0, jdk 21;Spring Reactive Web, Spring for Apache Kafka, lombok, reactor-kafka)

#### proj: kafka-cluster-setup
- package:section5 - demo fault tolerance when kafka nodes are down in a cluster 
- docker-compose.yaml, /props w/ (3) *.properties file to setup Kafka cluster w/ 3 nodes; generated another compose version to use bitnami image

#### proj: kafka-commands
- added file for reset offset cmds (kafka-consumer-groups.sh) --reset-offsets, --shift-by, --by-duration, 
--to-datetime, --to-earliest, --to-latest, --to-current, --execute, --dry-run
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

#### repo: event-driven-microservices-kafka
- updated readme