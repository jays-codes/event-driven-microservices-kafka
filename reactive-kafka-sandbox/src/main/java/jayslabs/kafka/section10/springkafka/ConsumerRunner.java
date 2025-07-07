package jayslabs.kafka.section10.springkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;



@Service
public class ConsumerRunner implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(ConsumerRunner.class);
    
    @Autowired
    private ReactiveKafkaConsumerTemplate<String, ConsumerOrderDTO> consumerTemplate;


    @Override
    public void run(String... args) throws Exception {
        this.consumerTemplate.receive()
        //.doOnNext(r -> log.info("Received record - key: {}, value: {}, partition: {}, offset: {}", 
        //         r.key(), r.value().customerId(), r.partition(), r.offset()))
        .doOnNext(r -> log.info("key: {}, value: {}", r.key(), r.value()))
        //.doOnNext(r -> r.headers().forEach(h -> log.info("header key: {}, value: {}", h.key(), new String(h.value()))))
        // .doOnError(ex -> log.error("Error processing record: {}", ex.getMessage()))
        // .onErrorResume(ex -> {
        //     log.warn("Skipping problematic record and continuing...");
        //     return this.consumerTemplate.receive(); // Continue with next records
        // })
        .subscribe();
    }
}
