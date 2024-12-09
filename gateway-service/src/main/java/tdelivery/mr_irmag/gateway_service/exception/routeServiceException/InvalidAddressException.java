package tdelivery.mr_irmag.gateway_service.exception.routeServiceException;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(String message) {
        super(message);
    }
}

