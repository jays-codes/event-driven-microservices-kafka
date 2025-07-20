package jayslabs.kafka.products.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductViewEvent {
    private Integer productId;
    //browser
    //location
}