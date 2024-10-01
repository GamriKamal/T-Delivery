package tdelivery.mr_irmag.auth_service.Exceptions;

import java.io.Serializable;

public class UsernameAlreadyExistsException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE = "Username already exists";

    public UsernameAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyExistsException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
