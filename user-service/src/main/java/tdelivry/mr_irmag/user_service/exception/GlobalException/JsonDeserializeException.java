package tdelivry.mr_irmag.user_service.exception.GlobalException;

public class JsonDeserializeException extends RuntimeException {
    public JsonDeserializeException(String message) {
        super(message);
    }
}
