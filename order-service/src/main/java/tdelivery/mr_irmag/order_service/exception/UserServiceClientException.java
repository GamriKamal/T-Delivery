package tdelivery.mr_irmag.order_service.exception;

public class UserServiceClientException extends RuntimeException {
  private final String userId;

  public UserServiceClientException(String message, String userId, Throwable cause) {
    super(message, cause);
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  @Override
  public String toString() {
    return String.format("UserServiceClientException: userId=%s, message=%s", userId, getMessage());
  }
}

