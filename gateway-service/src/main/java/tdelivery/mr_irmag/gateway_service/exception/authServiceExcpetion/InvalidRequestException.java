package tdelivery.mr_irmag.gateway_service.exception.authServiceExcpetion;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
