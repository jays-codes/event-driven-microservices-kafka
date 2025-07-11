package jayslabs.kafka.section9.transactions;

import java.time.Duration;
import java.util.function.Predicate;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

public class TransferEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(TransferEventProcessor.class);

    private final KafkaSender<String, String> sender;

    public TransferEventProcessor(KafkaSender<String, String> sender) {
        this.sender = sender;
    }

    // old implementation
    public Flux<SenderResult<String>> process(Flux<TransferEvent> flux){
        return flux
        .concatMap(this::validate)
        .concatMap(this::sendTransaction);
    }


    // public Flux<Void> process(Flux<TransferEvent> flux){
    //     return flux
    //     .concatMap(this::validate)
    //     .concatMap(this::useSendTransactionally);
    // }

    private Mono<SenderResult<String>> sendTransaction(TransferEvent event){
        var senderRecords = this.toSenderRecords(event);

        var manager = this.sender.transactionManager();
        return manager.begin()
        .then(
            this.sender
            .send(senderRecords)
            .concatWith(
                Mono.delay(Duration.ofSeconds(1)).then(
                    Mono.fromRunnable(event.acknowledge())
                )
            )
            .concatWith(manager.commit())
            .last()
        )
        .doOnError(ex -> log.error(ex.getMessage()))
        .onErrorResume(ex -> manager.abort());
    
    }


    /**
     * Alternative implementation using KafkaSender.sendTransactionally()
     * which handles transaction lifecycle automatically (begin/commit/abort)
     */
    private Mono<Void> useSendTransactionally(TransferEvent event){
        var senderRecords = this.toSenderRecords(event);

        return this.sender.sendTransactionally(Mono.just(senderRecords))
//        .then(Mono.delay(Duration.ofSeconds(3)))
//        .then(Mono.fromRunnable(event::acknowledge))
        .then()
        .doOnError(ex -> log.error("Transaction failed: {}", ex.getMessage()));
        // Note: sendTransactionally() automatically handles abort on error
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

