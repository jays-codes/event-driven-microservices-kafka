package jayslabs.kafka.section9.transactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

public class TransferEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final KafkaReceiver<String, String> receiver;

    public Flux<TransferEvent> receive(){
        return this.receiver.receive()
        .doOnNext(r -> log.info("Received record: {}", r.value()))
        .map(this::toTransferEvent);
    }
    
    public TransferEventConsumer(KafkaReceiver<String, String> receiver) {
        this.receiver = receiver;
    }

    /*
    
     */
    private TransferEvent toTransferEvent(ReceiverRecord<String, String> record) {
        //assume 1:a:c:100
        var arr = record.value().split(",");
        var reckey = record.key();

        //Simulate a failed TransferEvent at key = 6 
        var runnable = reckey.equals("6") ? this.fail(record) : this.acknowledge(record);
        
        return new TransferEvent(
            reckey,
            arr[1],
            arr[2],
            Integer.parseInt(arr[3]), //amount
            runnable
        );
    }

    private Runnable acknowledge(ReceiverRecord<String, String> record) {
        return () -> record.receiverOffset().acknowledge();
    }

    private Runnable fail(ReceiverRecord<String, String> record) {
        return () -> {
            throw new RuntimeException("Error while ack");
        };
    }

}
