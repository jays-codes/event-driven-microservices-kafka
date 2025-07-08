package jayslabs.kafka.section11.integrationtest.producer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class ProducerRunner implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(ProducerRunner.class);

    @Autowired
    private ReactiveKafkaProducerTemplate<String, OrderEventDTO> template;

    @Override
    public void run(String... args) throws Exception {
        this.generateOrderEvents()
        .flatMap(oe -> template.send("order-events", oe.orderId().toString(), oe))
        .doOnNext(r -> log.info("Result: {}", r.recordMetadata()))
        .subscribe();
    }

    private Flux<OrderEventDTO> generateOrderEvents(){
        return Flux.interval(Duration.ofMillis(100))
        .take(25)
        .map(i -> new OrderEventDTO(UUID.randomUUID(), i, LocalDateTime.now()));
    }
}
