package jayslabs.kafka.section9.transactions;

import java.util.function.Predicate;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

public class TransferEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(TransferEventProcessor.class);

    private final KafkaSender<String, String> sender;

    public TransferEventProcessor(KafkaSender<String, String> sender) {
        this.sender = sender;
    }

    public void process(Flux<TransferEvent> events){
    
    }

    //simulate not enough money error at key =5 
    private Mono<TransferEvent> validate(TransferEvent event){
        return Mono.just(event)
        .filter(Predicate.not(e -> e.key().equals("5")))
        .switchIfEmpty(
            Mono.<TransferEvent>fromRunnable(event.acknowledge())
            .doFirst(() -> log.info("fails validation: {}", event.key()))
        );
    }

    private Flux<SenderRecord<String, String, String>> toSenderRecords(TransferEvent event){
        //create event to credit to account
        var prodRec1 = new ProducerRecord<>(
            "transaction-events",
            event.key(),
            "%s+%s".formatted(event.to(), event.amount())
        );

        //create event to debit from account
        var prodRec2 = new ProducerRecord<>(
            "transaction-events",
            event.key(),
            "%s-%s".formatted(event.from(), event.amount())
        );

        var sendRec1 = SenderRecord.create(prodRec1, prodRec1.key());
        var sendRec2 = SenderRecord.create(prodRec2, prodRec2.key());

        return Flux.just(sendRec1, sendRec2);
    }
}

