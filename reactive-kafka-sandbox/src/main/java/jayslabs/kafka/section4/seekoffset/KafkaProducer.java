package jayslabs.kafka.section4.seekoffset;

import java.time.Duration;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.core.publisher.Flux;


public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    
    public static void main(String[] args) {

        var producerConfig = Map.<String, Object>of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class

        );

        var senderOptions = SenderOptions.<String, String>create(producerConfig);

        var flux = Flux.interval(Duration.ofMillis(50))
        .take(10_000)
        .map(i -> new ProducerRecord<>("order-events", i.toString(), "order-event-" + i))
        .map(precord -> SenderRecord.create(precord, precord.key()));

        var sender = KafkaSender.create(senderOptions);
        sender.send(flux)
        
        .doOnComplete(sender::close)
        .subscribe();

    }
}
