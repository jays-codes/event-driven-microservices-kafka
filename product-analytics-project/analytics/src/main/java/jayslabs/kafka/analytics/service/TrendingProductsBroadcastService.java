package jayslabs.kafka.analytics.service;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jayslabs.kafka.analytics.dto.ProductTrendingDTO;
import jayslabs.kafka.analytics.repository.ProductViewRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TrendingProductsBroadcastService {
    private final ProductViewRepository repo;
    private Flux<List<ProductTrendingDTO>> trends;

    public Flux<List<ProductTrendingDTO>> getTrends() {
        return this.trends;
    }

    @PostConstruct
    private void init() {
        this.trends = this.repo.findTop5ByOrderByCountDesc()
        .map(pvc -> new ProductTrendingDTO(pvc.getId(), pvc.getCount()))
        .collectList()
        .filter(Predicate.not(List::isEmpty))
        .repeatWhen(l -> l.delayElements(Duration.ofSeconds(5)))
        .distinctUntilChanged()
        .cache(1);
    }
}
