package tdelivery.mr_irmag.auth_service.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
