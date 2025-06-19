package jayslabs.kafka.section4.seekoffset;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
/* 

Read kafka messages from specific offset

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
            //ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true,
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName()
        );

        var receiverOptions = ReceiverOptions
        .create(consumerConfig)

        .addAssignListener(c -> {
            c.forEach(r-> log.info("Current offset of partition - {}: {}", 
                r.topicPartition(), r.position()));
            
            //seek to the last 2 messages
            // c.forEach(r-> r.seek(r.position()-2));

            seekToLastNMessagesForPartition(c, 2, 3);
        })

        
        .subscription(List.of("order-events"));


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

    private static void seekToLastNMessagesForPartition(Collection<ReceiverPartition> partitions, int partitionNumber, int messageCount) {
        partitions.stream()
            .filter(r -> r.topicPartition().partition() == partitionNumber)
            .findFirst()
            .ifPresent(r -> r.seek(r.position() - messageCount));
    }

}
