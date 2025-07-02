package jayslabs.kafka.section9.transactions;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

public class TransferDemo {
    private static final Logger log = LoggerFactory.getLogger(TransferDemo.class);
    public static void main(String[] args) {
        var txferEventConsumer = new TransferEventConsumer(kafkaReceiver());
        var txferEventProcessor = new TransferEventProcessor(kafkaSender());

        txferEventConsumer
        .receive()
        .transform(txferEventProcessor::process)
        .doOnNext(r -> log.info("transfer success: {}", r.correlationMetadata()))
        .doOnError(e -> log.error("transfer failed: {}", e.getMessage()))
        .subscribe();


    }


    private static KafkaReceiver<String,String> kafkaReceiver(){
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

        var receiverOptions = ReceiverOptions.<String,String>create(consumerConfig)
        .subscription(List.of("transfer-requests"));

        return KafkaReceiver.create(receiverOptions);
    }
    
    private static KafkaSender<String,String> kafkaSender(){
        var producerConfig = Map.<String, Object>of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );

        var senderOptions = SenderOptions.<String, String>create(producerConfig);
        return KafkaSender.create(senderOptions);
    }
}
