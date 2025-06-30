package jayslabs.kafka.section8.poisonpillmessages;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;


/*
 
    Poison Pill Messages - 
    - simulate poison pill messagesvia mismatch in k,v serializer/deserializer type

 */

public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    public static void main(String[] args) {

        var kafkaReceiver = kafkaReceiver();

        kafkaReceiver
            .receive()
            .doOnNext(r -> {
                log.info("key: {}, value: {}", r.key(), r.value());
            })
            .doOnNext(r -> r.receiverOffset().acknowledge())
            .subscribe();
    }

    private static KafkaReceiver<String,Integer> kafkaReceiver(){
        var consumerConfig = Map.<String, Object>of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, "inventory-service-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "1",
//            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true,
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName()
        );

        var receiverOptions = ReceiverOptions.<String,Integer>create(consumerConfig)
        .subscription(List.of("order-events"));


        return KafkaReceiver.create(receiverOptions);
    }
    

}
