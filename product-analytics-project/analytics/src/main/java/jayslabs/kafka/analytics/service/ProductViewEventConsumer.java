package jayslabs.kafka.analytics.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jayslabs.kafka.analytics.entity.ProductViewCount;
import jayslabs.kafka.analytics.event.ProductViewEvent;
import jayslabs.kafka.analytics.repository.ProductViewRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@AllArgsConstructor
public class ProductViewEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductViewEventConsumer.class);

    private final ReactiveKafkaConsumerTemplate<String, ProductViewEvent> template;
    private final ProductViewRepository pvrepo;

    @PostConstruct
    public void subscribe() {
        this.template.receive()
        .bufferTimeout(1000, Duration.ofSeconds(1))
        .flatMap(this::process)
        .subscribe();
    }

    private Mono<Void> process(List<ReceiverRecord<String, ProductViewEvent>> events) {
        var evtMap = events.stream()
        .map(r -> r.value().getProductId())
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        /*
         This will produce evtMap
            {
                1: 10,
                2: 20,
                3: 30
            }
        This is a map of productId and count of views.
        If the productId is already present, then we need to update the count by adding the new count to the existing count.
        If the productId is not present, then we need to insert the productId and count into the database.
        */

        /**
         * get
         */
        return this.pvrepo
        .findAllById(evtMap.keySet())
        .collectMap(ProductViewCount::getId) //Mono<Map<Integer, ProductViewCount>>
        .defaultIfEmpty(Collections.emptyMap())
        .map(dbMap -> evtMap.keySet().stream()
           .map(productId -> updateViewCount(dbMap, evtMap, productId))
           .collect(Collectors.toList()))
        .flatMapMany(this.pvrepo::saveAll)
        .doOnComplete(() -> events.get(events.size() - 1).receiverOffset().acknowledge())
        .doOnError(e -> logger.error(e.getMessage()))
        .then();
    }

    private ProductViewCount updateViewCount(Map<Integer, ProductViewCount> dbMap, 
    Map<Integer, Long> evtMap, int productId) {
        var pvc = dbMap.getOrDefault(productId, new ProductViewCount(productId, 0L, true));
        pvc.setCount(pvc.getCount() + evtMap.getOrDefault(productId, 0L));
        pvc.setNew(false);
        return pvc;
    }
}
