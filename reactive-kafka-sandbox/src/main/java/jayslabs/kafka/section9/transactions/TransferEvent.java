package jayslabs.kafka.section9.transactions;

public record TransferEvent(
    String key,
    String from,
    String to,
    int amount,
    Runnable acknowledge
) {

}
