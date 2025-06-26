package jayslabs.kafka.section8.errhandling;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

/*
 
Error Handling in Kafka Consumer - Separate Receiver and Processor pipeline; 
customized Retry depending on Exception type; simulate DB down for key 5

 */

public class KafkaConsumerV3 {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerV3.class);
    public static void main(String[] args) {

        var consumerConfig = Map.<String, Object>of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "inventory-service-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "1",
//            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true,
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName()
        );

        var receiverOptions = ReceiverOptions.create(consumerConfig)
        .subscription(List.of("order-events"));


        KafkaReceiver.create(receiverOptions)
            .receive()
            .log()
            .concatMap(KafkaConsumerV3::processMessage)
            .subscribe();
    }

    private static Mono<Void> processMessage(ReceiverRecord<Object,Object> record) {
        return Mono.just(record)
        .doOnNext(r -> {
            if (r.key().toString().equals("5")) {
                throw new RuntimeException("DB is down");
            }
            var index = ThreadLocalRandom.current().nextInt(1, 100);
            log.info("Processing message - index: {}, topic: {}, partition: {}, offset: {}, key: {}, value: {}", 
            index, r.topic(), r.partition(), r.offset(), r.key(), r.value().toString().toCharArray()[index]);
            r.receiverOffset().acknowledge();
        })
        .retryWhen(
            useCustomRetrySpec()
        )
        .doOnError(ex -> log.error(ex.getMessage()))
        .onErrorResume(IndexOutOfBoundsException.class, 
            ex -> Mono.fromRunnable(
                () -> record.receiverOffset().acknowledge()))
        .then();
    }

    private static Retry useCustomRetrySpec(){
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
        .filter(IndexOutOfBoundsException.class::isInstance)
        .onRetryExhaustedThrow((retrySpec, signal) -> 
        signal.failure()
        );
    }
}
