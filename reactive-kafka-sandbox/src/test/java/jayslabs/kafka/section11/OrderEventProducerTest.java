package jayslabs.kafka.section11;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;

import jayslabs.kafka.AbstractIntegrationTest;
import jayslabs.kafka.section11.integrationtest.producer.OrderEventDTO;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.test.StepVerifier;

@TestPropertySource(properties = "app=producer")
public class OrderEventProducerTest extends AbstractIntegrationTest {

    private final Logger log = LoggerFactory.getLogger(OrderEventProducerTest.class);

    @Test
    void producerTest() {
        KafkaReceiver<String, OrderEventDTO> receiver = createReceiver("order-events");
        var orderEvents = receiver.receive()
        .take(10)
        .doOnNext(r -> log.info("key: {}, value: {}", r.key(), r.value()))
        ;

        StepVerifier.create(orderEvents)
        .consumeNextWith(r -> Assertions.assertNotNull(r.value().orderId()))
        .expectNextCount(9)
        .expectComplete()
        .verify(Duration.ofSeconds(10));
    }
}
