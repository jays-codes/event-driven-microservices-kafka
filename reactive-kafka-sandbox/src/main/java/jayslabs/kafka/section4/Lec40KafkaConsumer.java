package jayslabs.kafka.section4;

//Simple Kafka consumer using reactor kafka

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

/** 
producer ----> kafka broker <----> consumer


* */
public class Lec40KafkaConsumer {


    private static final Logger log = LoggerFactory.getLogger(Lec40KafkaConsumer.class);
    public static void main(String[] args) {

        var consumerConfig = Map.<String, Object>of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "sandbox-group-1"
        );

        var receiverOptions = ReceiverOptions
        .create(consumerConfig)
        .subscription(List.of("test-topic-3"));


        KafkaReceiver.create(receiverOptions)
            .receive()
            .doOnNext(record -> {
                log.info("Received message - key: {}, value: {}", 
                record.key(), record.value());
            })
            .subscribe();


    }
}
