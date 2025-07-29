package jayslabs.kafka.analytics.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jayslabs.kafka.analytics.entity.ProductViewCount;
import jayslabs.kafka.analytics.event.ProductViewEvent;
import jayslabs.kafka.analytics.repository.ProductViewRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

/**
 * Service responsible for processing ProductViewEvent batches from Kafka.
 * 
 * Single Responsibility: Handles WRITE operations for analytics data
 * - Aggregates incoming view events by product ID
 * - Performs upsert operations on product view counts
 * - Manages Kafka offset acknowledgments
 * 
 * Note: This service does NOT handle trending data broadcasts.
 * See TrendingProductsBroadcastService for READ operations and real-time streaming.
 */
@Service
@AllArgsConstructor
public class ProductViewAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(ProductViewAnalyticsService.class);

    private final ProductViewRepository pvrepo;
    
    public Mono<Void> processBatch(List<ReceiverRecord<String, ProductViewEvent>> events) {

        //This will produce evtMap
        var evtMap = events.stream()
        .map(r -> r.value().getProductId())
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //This will produce dbMap
        return this.pvrepo
        .findAllById(evtMap.keySet())
        .collectMap(ProductViewCount::getId) //Mono<Map<Integer, ProductViewCount>>
        .defaultIfEmpty(Collections.emptyMap())

        //This will produce pvcList and save it to the database
        .map(dbMap -> evtMap.keySet().stream()
           .map(productId -> updateViewCount(dbMap, evtMap, productId))
           .collect(Collectors.toList()))
        .flatMapMany(this.pvrepo::saveAll)

        //This will acknowledge the last event
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
