package tdelivery.mr_irmag.order_service.exception;

import lombok.ToString;

@ToString(callSuper = true)
public class UserServiceClientException extends RuntimeException {
    private final String userId;

    public UserServiceClientException(String message, String userId, Throwable cause) {
        super(message, cause);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}

