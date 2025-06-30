package jayslabs.kafka.section8.poisonpillmessages;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;


public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    
    public static void main(String[] args) {

        var producerConfig = Map.<String, Object>of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class
        );

        var senderOptions = SenderOptions.<String, Integer>create(producerConfig);

        var flux = Flux.range(1, 100)
                    .map(i -> new ProducerRecord<>("order-events", i.toString(), i))
                    .map(pr -> SenderRecord.create(pr, pr.key()));

        var sender = KafkaSender.create(senderOptions);
        sender.send(flux)
        .doOnNext(r -> {
            log.info("Processed: {}", r.correlationMetadata());
        })
        .doOnComplete(sender::close)
        .subscribe();

    }

}
