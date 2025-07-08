package jayslabs.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

@EmbeddedKafka(
	ports = 9092,
	partitions = 1,
	brokerProperties = { "auto.create.topics.enable=false" },
	topics = { "order-events" }
//	brokerProperties = { "listeners=PLAINTEXT://localhost:9092" }
)
class EmbeddedKafkaSandboxTests {

	@Test
	void embeddedKafkaDemo() {
		StepVerifier.create(TestProducer.run())
		.verifyComplete();

		StepVerifier.create(TestConsumer.run())
		.verifyComplete();
	}


	private static class TestProducer{
		private static final Logger log = LoggerFactory.getLogger(TestProducer.class);
	
		public static Mono<Void> run() {
			var producerConfig = Map.<String, Object>of(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
			);
	
			var senderOptions = SenderOptions.<String, String>create(producerConfig);
	
			var flux = Flux.range(1,10)
			.delayElements(Duration.ofMillis(100))
			.map(i -> new ProducerRecord<>("order-events", i.toString(), "order-" + i))
			.map(pr -> SenderRecord.create(pr, pr.key()));
	
			var sender = KafkaSender.create(senderOptions);
			return sender.send(flux)
			.doOnNext(r -> {
				log.info("Processed: {}", r.correlationMetadata());
			})
			.doOnComplete(sender::close)
			.then();
		}
	}

	private static class TestConsumer{
		private static final Logger log = LoggerFactory.getLogger(TestConsumer.class);

		public static Mono<Void> run() {
	
			var consumerConfig = Map.<String, Object>of(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
				ConsumerConfig.GROUP_ID_CONFIG, "demo-group-123",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
				ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "1",
				ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName()
			);
	
			var receiverOptions = ReceiverOptions.<String, String>create(consumerConfig)
			.subscription(List.of("order-events"));
	
			return KafkaReceiver.create(receiverOptions)
				.receive()
				.take(10)
				.doOnNext(record -> {
					log.info("key: {}, value: {}", record.key(), record.value());
				})
	
				.doOnNext(record -> record.receiverOffset().acknowledge())
				.then();
	
	
		}
	}

}
