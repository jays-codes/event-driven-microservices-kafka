package jayslabs.kafka.analytics.config;

import java.util.List;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import jayslabs.kafka.analytics.event.ProductViewEvent;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, ProductViewEvent> receiverOptions(KafkaProperties properties) {
        return ReceiverOptions.<String, ProductViewEvent>create(properties.buildConsumerProperties())
        .consumerProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductViewEvent.class)
        .consumerProperty(JsonDeserializer.USE_TYPE_INFO_HEADERS, false)
        .subscription(List.of("product-view-events"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, ProductViewEvent> kafkaConsumerTemplate(ReceiverOptions<String, ProductViewEvent> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
