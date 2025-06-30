package jayslabs.kafka.section8.deadlettertopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

public class OrderEventProcessor {
    private final DLTProducer<String,String> dltProducer;

    private static final Logger log = LoggerFactory.getLogger(OrderEventProcessor.class);

    public OrderEventProcessor(DLTProducer<String,String> dltProducer) {
        this.dltProducer = dltProducer;
    }

    public Mono<Void> process(ReceiverRecord<String,String> record) {
        return Mono.just(record)
        .doOnNext(r -> {
            if (r.key().toString().endsWith("5")) {
                throw new RuntimeException("Processing Exception");
            }
            log.info("key: {}, value: {}", r.key(), r.value());
            r.receiverOffset().acknowledge();
        })
        .onErrorMap(ex -> new RecordProcessingException(record, ex))
        .transform(this.dltProducer.recordProcessingErrorHandler())
        .then();
    }
}
