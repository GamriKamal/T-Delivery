package tdelivery.mr_irmag.order_service.exception;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException(String message) {
        super(message);
    }
}
