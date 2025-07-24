package jayslabs.kafka.products.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import jayslabs.kafka.products.event.ProductViewEvent;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderRecord;

@AllArgsConstructor
public class ProductViewEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(ProductViewEventProducer.class);

    private final ReactiveKafkaProducerTemplate<String, ProductViewEvent> template;
    private final Sinks.Many<ProductViewEvent> sink;
    private final Flux<ProductViewEvent> flux;
    private final String topic;


    public void subscribe() {
        var srflux = this.flux
        .map(evt -> new ProducerRecord<>(topic, evt.getProductId().toString(), evt))
        .map(pr -> SenderRecord.create(pr, pr.key()));

        this.template.send(srflux)
        .doOnNext(r -> logger.info("Emitted event: {}", r.correlationMetadata()))
        .subscribe();
    }

    public void emitEvent(ProductViewEvent event) {
        this.sink.tryEmitNext(event);
    }

}
