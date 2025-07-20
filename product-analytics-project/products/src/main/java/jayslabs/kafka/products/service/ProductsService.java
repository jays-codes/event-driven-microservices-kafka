package jayslabs.kafka.products.service;

import jayslabs.kafka.products.dto.ProductDTO;
import reactor.core.publisher.Mono;

public interface ProductsService {

    /*
     * @param productId
     * @return ProductDTO
     */
    Mono<ProductDTO> getProduct(int productId);
}
