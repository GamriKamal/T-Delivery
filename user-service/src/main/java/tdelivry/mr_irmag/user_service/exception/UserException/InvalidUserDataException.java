package tdelivry.mr_irmag.user_service.exception.UserException;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}