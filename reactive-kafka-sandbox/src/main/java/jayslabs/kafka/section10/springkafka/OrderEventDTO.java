package jayslabs.kafka.section10.springkafka;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderEventDTO(
    UUID orderId,
    long customerId,
    LocalDateTime orderDate
) {

}
