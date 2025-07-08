package jayslabs.kafka.section11.integrationtest.producer;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderEventDTO(
    UUID orderId,
    long customerId,
    LocalDateTime orderDate
) {

}
