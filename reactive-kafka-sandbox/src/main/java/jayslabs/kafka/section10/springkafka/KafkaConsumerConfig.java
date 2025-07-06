package jayslabs.kafka.section10.springkafka;

import java.util.List;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, OrderEventDTO> receiverOptions(KafkaProperties kafkaProperties){
        return ReceiverOptions.<String, OrderEventDTO>create(kafkaProperties.buildConsumerProperties())
        .consumerProperty(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, "false")
        .subscription(List.of("order-events"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, OrderEventDTO> consumerTemplate(ReceiverOptions<String, OrderEventDTO> receiverOptions){
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
