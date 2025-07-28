package jayslabs.kafka.analytics.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jayslabs.kafka.analytics.entity.ProductViewCount;
import reactor.core.publisher.Flux;

@Repository
public interface ProductViewRepository extends ReactiveCrudRepository<ProductViewCount, Integer> {

    Flux<ProductViewCount> findTop5ByOrderByCountDesc();
}
