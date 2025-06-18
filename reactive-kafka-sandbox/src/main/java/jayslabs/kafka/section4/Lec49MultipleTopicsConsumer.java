package jayslabs.kafka.section4;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

public class Lec49MultipleTopicsConsumer {
    private static final Logger log = LoggerFactory.getLogger(Lec49MultipleTopicsConsumer.class);
    public static void main(String[] args) {

        var consumerConfig = Map.<String, Object>of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "inventory-service-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "instance-1",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true
        );

        var receiverOptions = ReceiverOptions
        .create(consumerConfig)
        //.subscription(List.of("order.*"));
        .subscription(Pattern.compile("order.*"));


        KafkaReceiver.create(receiverOptions)
            .receive()
            .doOnNext(record -> {
                //acknowledge the message
                log.info("Received message - topic: {}, partition: {}, offset: {}, key: {}, value: {}", 
                record.topic(), record.partition(), record.offset(), record.key(), record.value());

            })
            .doOnNext(record -> record.receiverOffset().acknowledge())
            .subscribe();


    }

}
