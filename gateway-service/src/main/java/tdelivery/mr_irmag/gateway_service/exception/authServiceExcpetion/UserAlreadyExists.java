package tdelivery.mr_irmag.gateway_service.exception.authServiceExcpetion;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String message) {
        super(message);
    }
}
