package jayslabs.kafka.products.service;

import org.springframework.stereotype.Service;

import jayslabs.kafka.products.dto.ProductDTO;
import jayslabs.kafka.products.event.ProductViewEvent;
import jayslabs.kafka.products.mapper.ProductsMapper;
import jayslabs.kafka.products.repository.ProductRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductsServiceImpl implements ProductsService {

    private final ProductRepository productRepository;
    private final ProductViewEventProducer productViewEventProducer;

    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        return productRepository.findById(productId)
                .doOnNext(p -> productViewEventProducer.emitEvent(new ProductViewEvent(p.getId())))
                .map(ProductsMapper::toDTO);
    }
}
