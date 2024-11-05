package tdelivery.mr_irmag.order_service.exception;

public class KafkaException extends RuntimeException {
    public KafkaException(String message) {
        super(message);
    }
}
