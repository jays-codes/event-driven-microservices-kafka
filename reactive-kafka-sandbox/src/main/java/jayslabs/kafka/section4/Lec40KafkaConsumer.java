package jayslabs.kafka.section4;

//Simple Kafka consumer using reactor kafka

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

/** 
producer ----> kafka broker <----> consumer


* */
public class Lec40KafkaConsumer {

    public static void main(String[] args) {

        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "sandbox-group-1"
        );

    }
}
