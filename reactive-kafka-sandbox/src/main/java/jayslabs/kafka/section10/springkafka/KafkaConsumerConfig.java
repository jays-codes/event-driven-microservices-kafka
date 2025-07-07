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
    public ReceiverOptions<String, ConsumerOrderDTO> receiverOptions(KafkaProperties kafkaProperties){
        return ReceiverOptions.<String, ConsumerOrderDTO>create(kafkaProperties.buildConsumerProperties())
        .consumerProperty(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, "false")
        .consumerProperty(JsonDeserializer.USE_TYPE_INFO_HEADERS, "false")
        .consumerProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, ConsumerOrderDTO.class)
        .subscription(List.of("order-events"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, ConsumerOrderDTO> consumerTemplate(ReceiverOptions<String, ConsumerOrderDTO> receiverOptions){
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
