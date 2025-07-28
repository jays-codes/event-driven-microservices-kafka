# event-driven-microservices-kafka
Jay's project/practice repo for Event-driven Microservice using Kafka

#### demo proj: product-analytics-project

- product-analytics-project/analytics: [BP] created ProductViewEventConsumer class - Batch Processing Kafka consumer for product view analytics. reads Product View events from a kafka topic and does batch processing (1,000 events or 1 second - whichever comes first) to minimize on DB calls; created Sequence and Class Diagrams to illustrate flow and architecture
- product-analytics-project/analytics: created data.sql(product_view_count), application.yaml; created *.entity.ProductViewCount, *.event.ProductViewEvent, *.dto.ProductTrendingDTO, *.repository.<<ProductViewRepository>> (extends <<ReactiveCrudRepository<Product,Integer>>>), *.config.KafkaConsumerConfig,controller.TrendingController
- created analytics microservice project: product-analytics-project/analytics; jayslabs.kafka:analytics, jdk 21, sb 3.5.4; dep: Spring Reactive Web, Spring Data R2DBC, Spring for Apache Kafka, reactor-kafka, lombok, H2;

- product-analytics-project/products: updated controller api to return Mono<ResponseEntity<ProductDTO>>, updated Test class
- product-analytics-project/products: created integration test components: ProductsApplicationTests (extends <<AbstractIntegrationTest>>); uses WebTestClient
- product-analytics-project/products: created references to PVEP from ServiceImpl, Controller; defined @Bean for PVEP in KafkaProducerConfig
- [BP] product-analytics-project/products: created *.service.ProductViewEventProducer. This is autowired and invoked inside ProductServiceImpl to write events to kafka topic.
- product-analytics-project/products: created *.service.<<ProductService>> and ProductServiceImpl 
- product-analytics-project/products: created data.sql, product.csv, app.yaml; created *.entity.Product, *.dto.ProductDTO, *.repository.<<ProductRepository>> (extends <<ReactiveCrudRepository<Product,Integer>>>), *.mapper.ProductsMapper, *.config.KafkaProducerConfig, *.event.ProductViewEvent,  *.controller.ProductsController
- created product microservice project: product-analytics-project/products; jayslabs.kafka:products, jdk 21, sb 3.5.3; dep: Spring Reactive Web, Spring Data R2DBC, Spring for Apache Kafka, reactor-kafka, lombok, H2;
- Refer to README2.md

#### proj: reactive-kafka-sandbox (jayslabs.kafka; SpringBoot 3.5.0, jdk 21;Spring Reactive Web, Spring for Apache Kafka, lombok, reactor-kafka)

- section11.integrationtest: created OrderEventConsumerTest (extends AIT). Testing for: msg is sent to topic and asserts on value written to log
- section11.integrationtest: [BP] updated AIT to add createSender() util methods and toSenderRecord():<K,V> SenderRecord<K,V,K> 
- section11.integrationtest: [BP] used @DirtiesContext to reset Spring app context after test run
- section11.integrationtest: [BP] created OrderEventProducerTest extends AIT. Testing for: producer is running, 10 msgs published to topic, msgs can be deserialized as OrderEventDTO, msgs orderId is not null, all 10 msgs arrive within 10 secs
- section11.integrationtest; [BP - use this as template] created base class for integration tests that need to test Kafka functionality. It sets up an embedded Kafka broker for testing and provides helper methods to create Kafka consumers easily.: AbstractIntegrationTest(@SpringBootTest, @EmbeddedKafka) in src/test/java - has broker:EmbeddedKafkaBroker(@Autowired), createReceiver():<K,V> KafkaReceiver<K,V> that calls KafkaTestUtils, consumerProps()
- section11.integrationtest; restructured code for section10 into consumer/producer packages to facilitate testing
- Updated test to get Random port via EmbeddedKafkaCondition.getBroker()
- src/test - Created Test Class: @EmbeddedKafka(ports, partitions, brokerProperties, topics), TestConsumer and TestProducer inner classes, @Test

- section10.springkafka: [BP] UC: Decode Kafka message with Consumer Custom Type (different from Type specified in header); Created custom Consumer DTO type, updated Consumer code to use custom DTO, updated Kafka consumer properties ( .consumerProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, ConsumerOrderDTO.class), yaml -> "spring.json.value.default.type": "jayslabs.kafka.section10.springkafka.ConsumerOrderDTO")
- section10.springkafka: UC:add/log type info header - changes to ConsumerRunner(log Header info), ConsumerConfig(add consumerProperty)
- section10.springKafka: created ProducerRunner (@Service) with template:ReactiveKafkaProducerTemplate<String,OrderEventDTO> (@Autowired; implements <<CommandLineRunner>>) to test Spring Kafka config; generateOrderEvents()Flux<OrderEventDTO>; run() - calls generateOrderEvents() and process SenderResults
- section10.springKafka: created OrderEventDTO, modified ReceiverOptions, ReactiveKafkaConsumerTemplate to <String, OrderEventDTO>, modified application yaml value-deserializerto use JsonDeserializer type
- section10.springkafka: created ConsumerRunner (@Service) with consumerTemplate:ReactiveKafkaConsumerTemplate<String,String> (@Autowired; implements <<CommandLineRunner>>) to test Spring Kafka config
- section10.springkafka: created kafka entries in application.yaml; create KafkaConsumerConfig(@Configuration) with @Bean methods -receiverOptions(KafkaProperties):ReceiverOptions<String, String>, and consumerTemplate(ReceiverOptions<String,String>):ReactiveKafkaConsumerTemplate<String,String>

- section9.transactions: Updated TransferEventProcessor to implement useSendTransactionally() which uses KafkaSender.sendTransactionally()
- Demo Class Execution Steps:
    1.) Create topics by running line #1&2 of script Section9-transaction.sh
    2.) open kafka bash and run a console producer for transfer-request topic
    3.) open separate kafka bash for two console consumers to demo isolation-level setting
    4.) in producer console, run > 1:a,b,15 to test. Exceptions would happen at id=5 and 6 
- added kafka bash scripts, Section9-transaction.sh; Demo steps
- section9.transactions: created Demo class; added Mono.delay() in sendTransaction()
- section9.transactions: updated TransferEventProcessor to immplement KafkaSender.transactionManager() in new method sendTransaction(TransferEvent):Mono<SenderResult<String>>; implemented process() to return Flux<SenderResult>, calling validate() and sendTransaction() for each item in Flux<TransferEvent>
- section9.transactions: created TransferEventProcessor: has KafkaSender instance, methods: process(Flux<TransferEvent>), validate(TransferEvent):Mono<TransferEvent>, toSenderRecords(TransferEvent):Flux<SenderRecord<String,String,String>>
- section9.transactions: created TransferEvent dto/record, TransferEventConsumer: has KafkaReceiver instance, methods: receive():Flux<TransferEvent>, toTransferEvent() - Mapper type method to convert ReceiverRecord to TransferEvent, fail()/acknowledge() - helper methods for mapper

- section8.poisonpillmessages: [BP] implement custom Deserializer to handle poison pill messages; ErrorHandlingDeserializer<T>, setFailedDeserializationFunction(), ReceiverOptions.withValueDeserializer()
- section8.poisonpillmessages: UC: simulate Poison Pill messages via mismatch in message k,v serlealizer/deserializer mismatch
- section8.deadlettertopic: UC: reprocess messages in DLT from consumer
- section8.deadlettertopic: [BP] UC: send message that encountered exception during processing to Dead Letter topic: KafkaConsumer, OrderEventProcessor, DLTProducer.recordProcessingErrorHandler, RecordProcessingException; [BP] updated sequence and class diagrams
- section8.errhandling.KafkaConsumerV3: [BP] UC: retry, ack and continue only for specific Exception type. Other ex type has no rety, no ack, and immed fail (e.g DB down); simulated 2 exception types
- section8.errhandling.KafkaConsumerV2: [BP] Separate Receiver and Processor Pipelines. used retryWhen(), Retry.fixedDelay(attempts, duration), 
onRetryExhausted(signal -> RetrySignal.failure()), .doOnError(log), doFinally(ack offset), onErrorComplete();
Created class diagram in diagrams/section8/errorhandling
- section8.errhandling.KafkaConsumer: to demo error handling while processing msg in Consumer

- section7.parallelordered.KafkaConsumer: [BP] UC:enable ordering of messages with Parallel processing; used .groupBy(), GroupedFlux, flatMap() 
- section7.parallel.KafkaConsumer: [BP] used Bounded Elastic Scheduler to run processing (blocking) to a separate thread pool; .publishOn(Schedulers.boundedElastic())
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