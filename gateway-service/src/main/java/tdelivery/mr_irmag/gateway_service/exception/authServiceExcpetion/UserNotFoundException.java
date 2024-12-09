package tdelivery.mr_irmag.gateway_service.exception.authServiceExcpetion;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
