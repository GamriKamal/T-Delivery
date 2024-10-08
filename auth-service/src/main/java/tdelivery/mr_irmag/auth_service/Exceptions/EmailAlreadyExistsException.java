package tdelivery.mr_irmag.auth_service.Exceptions;

import java.io.Serializable;

public class EmailAlreadyExistsException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Email already exists";

    public EmailAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyExistsException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}