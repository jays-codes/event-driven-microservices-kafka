package jayslabs.kafka.section8.deadlettertopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.receiver.ReceiverRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

public class DLTProducer<K,V> {
    private static final Logger log = LoggerFactory.getLogger(DLTProducer.class);

    private final KafkaSender<K,V> sender;
    private final Retry retrySpec;

    public DLTProducer(KafkaSender<K,V> sender, Retry retrySpec) {
        this.sender = sender;
        this.retrySpec = retrySpec;
    }
    
    public Mono<SenderResult<K>> produce(ReceiverRecord<K,V> record) {
        var sr = createSenderRecord(record);
        return this.sender.send(Mono.just(sr)).next();
    }

    private SenderRecord<K,V,K> createSenderRecord(ReceiverRecord<K,V> record) {
        var pr = new ProducerRecord<K,V>(
            record.topic() + "-dlt", 
            record.key(), 
            record.value()
        );

        return SenderRecord.create(pr, pr.key());
    }

    public Function<Mono<ReceiverRecord<K,V>>, Mono<Void>> recordProcessingErrorHandler() {
        return mono -> mono
        .retryWhen(this.retrySpec)
        .onErrorMap( ex -> ex.getCause() instanceof RecordProcessingException, Throwable::getCause)
        .doOnError(ex -> log.error(ex.getMessage()))
        .onErrorResume(RecordProcessingException.class, ex -> this.produce(ex.getRecord())
        .then(Mono.fromRunnable(() -> ex.getRecord().receiverOffset().acknowledge())))
        .then();
    }

}
