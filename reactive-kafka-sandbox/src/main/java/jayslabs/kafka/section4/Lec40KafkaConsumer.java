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
            ConsumerConfig.GROUP_ID_CONFIG, "sandbox-group-3",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "instance-1",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true
        );

        var receiverOptions = ReceiverOptions
        .create(consumerConfig)
        .subscription(List.of("test-topic-3"));


        KafkaReceiver.create(receiverOptions)
            .receive()
            .doOnNext(record -> {
                //acknowledge the message
                log.info("Received message - partition: {}, offset: {}, key: {}, value: {}", 
                record.partition(), record.offset(), record.key(), record.value());

            })
            .doOnNext(record -> record.receiverOffset().acknowledge())
            .subscribe();


    }
}
