package jayslabs.kafka.products.mapper;

import org.springframework.beans.BeanUtils;

import jayslabs.kafka.products.dto.ProductDTO;
import jayslabs.kafka.products.entity.Product;

public class ProductsMapper {

    public static ProductDTO toDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }
}
