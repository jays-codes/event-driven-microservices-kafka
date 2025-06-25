package jayslabs.kafka.section7.parallelordered;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
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
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );

        var senderOptions = SenderOptions.<String, String>create(producerConfig);

        // var flux = Flux.interval(Duration.ofMillis(50))
        // .take(10_000)
        var flux = Flux.range(0, 200)
        .map(i -> createSenderRecord(i.intValue()));

        var sender = KafkaSender.create(senderOptions);
        sender.send(flux)
        .doOnNext(r -> {
            log.info("Processed: {}", r.correlationMetadata());
        })
        .doOnComplete(sender::close)
        .subscribe();

    }

    private static SenderRecord<String, String, String> createSenderRecord(Integer id) {
        var headers = new RecordHeaders();
        headers.add("client-id", "some-client-id".getBytes());
        headers.add("trace-id", "123".getBytes());
        
        var precord = new ProducerRecord<>("order-events", null, id.toString(), "order-event-" + id, headers);

        return SenderRecord.create(precord, precord.key());
    }
}
