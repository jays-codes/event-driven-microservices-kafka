package jayslabs.kafka.products.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer id;
    private String description;
    private Integer price;
}
