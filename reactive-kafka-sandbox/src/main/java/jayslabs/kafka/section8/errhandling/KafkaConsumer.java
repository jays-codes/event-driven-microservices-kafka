package jayslabs.kafka.section8.errhandling;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

/*
 
Error Handling in Kafka Consumer - during processing

 */

public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
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

        var receiverOptions = ReceiverOptions.<String, String>create(consumerConfig)
        .subscription(List.of("order-events"));


        KafkaReceiver.create(receiverOptions)
            .receive()
            .doOnNext(record -> {
                log.info("Received message - topic: {}, partition: {}, offset: {}, key: {}, value: {}", 
                record.topic(), record.partition(), record.offset(), record.key(), record.value().toString().toCharArray()[15]);
            })
            .doOnError(ex -> {
                log.error(ex.getMessage());
            })

            .doOnNext(record -> record.receiverOffset().acknowledge())
            .subscribe();


    }
}
