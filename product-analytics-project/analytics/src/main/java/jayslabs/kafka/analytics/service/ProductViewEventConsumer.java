package jayslabs.kafka.analytics.service;

import java.time.Duration;

import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jayslabs.kafka.analytics.event.ProductViewEvent;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductViewEventConsumer {

    //private static final Logger logger = LoggerFactory.getLogger(ProductViewEventConsumer.class);

    private final ReactiveKafkaConsumerTemplate<String, ProductViewEvent> template;
    private final ProductViewAnalyticsService analyticsService;

    @PostConstruct
    public void subscribe() {
        this.template.receive()
        .bufferTimeout(1000, Duration.ofSeconds(1))
        .flatMap(analyticsService::processBatch)
        .subscribe();
    }
}
