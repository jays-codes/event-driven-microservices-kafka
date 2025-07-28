package jayslabs.kafka.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTrendingDTO {
    private Integer productId;
    private Long viewCount;
}
