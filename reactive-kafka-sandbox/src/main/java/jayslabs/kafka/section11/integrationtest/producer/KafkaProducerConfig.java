package jayslabs.kafka.section11.integrationtest.producer;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SenderOptions<String, OrderEventDTO> senderOptions(KafkaProperties kafkaProperties){
        return SenderOptions.<String, OrderEventDTO>create(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, OrderEventDTO> producerTemplate(SenderOptions<String, OrderEventDTO> senderOptions){
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
