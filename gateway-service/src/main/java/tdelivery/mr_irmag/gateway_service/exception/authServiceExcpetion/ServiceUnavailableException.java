package tdelivery.mr_irmag.gateway_service.exception.authServiceExcpetion;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
