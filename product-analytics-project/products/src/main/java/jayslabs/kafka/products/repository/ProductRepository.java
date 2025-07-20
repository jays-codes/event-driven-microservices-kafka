package jayslabs.kafka.products.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jayslabs.kafka.products.entity.Product;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

}
