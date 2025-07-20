package jayslabs.kafka.products.service;

import org.springframework.stereotype.Service;

import jayslabs.kafka.products.dto.ProductDTO;
import jayslabs.kafka.products.mapper.ProductsMapper;
import jayslabs.kafka.products.repository.ProductRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductsServiceImpl implements ProductsService {

    private final ProductRepository productRepository;

    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        return productRepository.findById(productId)
                .map(ProductsMapper::toDTO);
    }
}
