package jayslabs.kafka.products.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import jayslabs.kafka.products.event.ProductViewEvent;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SenderOptions<String, ProductViewEvent> senderOptions(KafkaProperties properties) {
        return SenderOptions.create(properties.buildProducerProperties());
    }
    @Bean
    public ReactiveKafkaProducerTemplate<String, ProductViewEvent> reactiveKafkaProducerTemplate(SenderOptions<String, ProductViewEvent> senderOptions) {
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }


}
