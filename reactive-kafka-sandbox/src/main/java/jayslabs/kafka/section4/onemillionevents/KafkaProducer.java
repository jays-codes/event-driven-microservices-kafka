package jayslabs.kafka.section4.onemillionevents;

import java.time.Duration;
import java.util.Map;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    
    public static void main(String[] args) {

        var producerConfig = Map.<String, Object>of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.BATCH_SIZE_CONFIG, 16384,
            ProducerConfig.LINGER_MS_CONFIG, 10,
            ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"
        );

        var senderOptions = SenderOptions.<String, String>create(producerConfig)
        .maxInFlight(20_000);

        var flux = Flux.range(1, 1_000_000)
        .map(i -> new ProducerRecord<>("order-events", i.toString(), "order-event-" + i))
        .map(precord -> SenderRecord.create(precord, precord.key()));

        var start = System.currentTimeMillis();
        var sender = KafkaSender.create(senderOptions);
        sender.send(flux)
        // .doOnNext(senderResult -> {
        //     log.info("Sent message - topic: {}, partition: {}, offset: {}", 
        //     senderResult.recordMetadata().topic(), senderResult.recordMetadata().partition(), senderResult.recordMetadata().offset());
        // })

        //shows which message got processed
        .doOnNext(r -> {
            if (Integer.parseInt(r.correlationMetadata()) % 10000 == 0) {
                log.info("Processed: {}", r.correlationMetadata());
            }
        })
        .doOnComplete(() -> {
            log.info("Time taken: {} ms", System.currentTimeMillis() - start);
            sender.close();
        })
        .subscribe();

    }
}
