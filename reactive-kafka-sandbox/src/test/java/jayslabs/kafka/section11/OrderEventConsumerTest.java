package jayslabs.kafka.section11;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import jayslabs.kafka.AbstractIntegrationTest;
import jayslabs.kafka.section11.integrationtest.consumer.ConsumerOrderDTO;
import jayslabs.kafka.section11.integrationtest.producer.OrderEventDTO;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.test.StepVerifier;

@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = "app=consumer")
public class OrderEventConsumerTest extends AbstractIntegrationTest {

    private final Logger log = LoggerFactory.getLogger(OrderEventConsumerTest.class);

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void consumerTest(CapturedOutput output) {
        KafkaSender<String, OrderEventDTO> sender = createSender();

        var uuid = UUID.randomUUID();
        var orderEvent = new OrderEventDTO(uuid, 1, LocalDateTime.now());
        var consumerOrder = new ConsumerOrderDTO(uuid.toString(), "1");
        var sr = toSenderRecord("order-events", uuid.toString(), orderEvent);

        var mono = sender.send(Mono.just(sr))
        .then(Mono.delay(Duration.ofMillis(500)))
        .then();

        StepVerifier.create(mono)
        .verifyComplete();

        Assertions.assertTrue(output.getOut().contains(consumerOrder.toString()));
    }
}
