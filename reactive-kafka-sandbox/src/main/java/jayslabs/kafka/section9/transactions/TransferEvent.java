package jayslabs.kafka.section9.transactions;

public record TransferEvent(
    String key,
    String from,
    String to,
    String amount,
    Runnable acknowledge
) {

}
